import React, { useEffect, useMemo, useState } from 'react';
import { componentInject } from 'app/shared/util/typed-inject';
import { IRootStore } from 'app/stores';
import { observer } from 'mobx-react';
import { onValue, ref } from 'firebase/database';
import { ViewportList, ViewportListPropsBase } from 'react-viewport-list';

export interface IFirebaseListProps<T> extends StoreProps, ViewportListPropsBase {
  path: string;
  itemBuilder: (firebaseIndex: number) => React.ReactNode;
  pushDirection: 'front' | 'back';
  filter?: (firebaseIndex: number) => boolean;
  defaultSort?: (a: T, b: T) => number;
}

function FirebaseList<T>({ path, itemBuilder, pushDirection, filter, defaultSort, firebaseDb, ...rest }: IFirebaseListProps<T>) {
  const [indices, setIndices] = useState<number[]>(null);
  const [numItemsAdded, setNumItemsAdded] = useState(0);

  useEffect(() => {
    const listRef = ref(firebaseDb, path);
    const unsubscribe = onValue(listRef, snapshot => {
      if (!snapshot.val()) {
        // this happens for emtpy array, such as tumors for a new mutation
        return;
      }

      if (!indices) {
        const items = snapshot.val();
        const itemsWithIndices = items.map((item, index) => ({ ...item, index }));
        setIndices(
          defaultSort
            ? itemsWithIndices
                .sort((a, b) => {
                  const { aIndex, ...aWithoutIndex } = a;
                  const { bIndex, ...bWithoutIndex } = b;
                  return defaultSort(aWithoutIndex, bWithoutIndex);
                })
                .map(item => item.index)
            : itemsWithIndices.map(item => item.index)
        );
      } else if (snapshot.size !== indices.length + numItemsAdded) {
        setNumItemsAdded(snapshot.size - indices.length);
      }
    });

    return () => unsubscribe?.();
  }, [path, firebaseDb, indices, numItemsAdded, setNumItemsAdded]);

  const listItems = useMemo(() => {
    if (!indices) {
      return [];
    }

    const items: { item: JSX.Element; index: number }[] = [];

    const addedItemIndices: number[] = [];
    for (let i = 0; i < numItemsAdded; i++) {
      addedItemIndices.push(i + indices.length);
    }

    let allItemIndices: number[];
    if (pushDirection === 'front') {
      allItemIndices = [...addedItemIndices.reverse(), ...indices];
    } else if (pushDirection === 'back') {
      allItemIndices = [...indices, ...addedItemIndices];
    }

    for (const index of allItemIndices) {
      items.push({ item: <div key={index}>{itemBuilder(index)}</div>, index }); // is using index as key ok? I think it might since firebase order not chagning
    }
    return items;
  }, [indices, numItemsAdded, path]);

  function getList() {
    if (!filter) {
      return listItems;
    }
    return listItems.filter(item => filter(item.index));
  }

  return (
    <ViewportList {...rest} items={getList()}>
      {item => item.item}
    </ViewportList>
  );
}

const mapStoreToProps = ({ firebaseStore }: IRootStore) => ({
  firebaseDb: firebaseStore.firebaseDb,
});

type StoreProps = Partial<ReturnType<typeof mapStoreToProps>>;

export default componentInject(mapStoreToProps)(observer(FirebaseList));
