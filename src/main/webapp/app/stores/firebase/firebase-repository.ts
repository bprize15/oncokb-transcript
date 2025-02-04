import { push, ref, remove, runTransaction, set, update, get, Database } from 'firebase/database';
import FirebaseAppStore from './firebase-app.store';

export const throwMissingFirebaseDBError = () => {
  throw new Error('No firebaseDb');
};

export class FirebaseRepository {
  private firebaseAppStore: FirebaseAppStore;

  constructor(firebaseAppStore: FirebaseAppStore) {
    this.firebaseAppStore = firebaseAppStore;
  }

  create = async (path: string, value: unknown) => {
    if (this.firebaseAppStore.firebaseDb) {
      return await set(ref(this.firebaseAppStore.firebaseDb, path), value);
    } else {
      throwMissingFirebaseDBError();
    }
  };

  get = async (path: string) => {
    return await get(ref(this.firebaseAppStore.firebaseDb as Database, path));
  };

  update = async (path: string, value: any) => {
    if (this.firebaseAppStore.firebaseDb) {
      return await update(ref(this.firebaseAppStore.firebaseDb, path), value);
    } else {
      throwMissingFirebaseDBError();
    }
  };

  delete = async (path: string) => {
    if (this.firebaseAppStore.firebaseDb) {
      return await remove(ref(this.firebaseAppStore.firebaseDb, path));
    } else {
      throwMissingFirebaseDBError();
    }
  };

  push = async (path: string, value: any) => {
    if (this.firebaseAppStore.firebaseDb) {
      const listRef = ref(this.firebaseAppStore.firebaseDb, path);
      const newItemRef = push(listRef);
      return await set(newItemRef, value);
    } else {
      throwMissingFirebaseDBError();
    }
  };

  pushMultiple = async (path: string, items: unknown[]) => {
    if (this.firebaseAppStore.firebaseDb) {
      const listRef = ref(this.firebaseAppStore.firebaseDb, path);
      const pushUpdates = {};
      items.forEach(item => {
        const postKey = push(listRef).key;
        if (postKey !== null) {
          pushUpdates[postKey] = item;
        }
      });
      return await update(listRef, pushUpdates);
    } else {
      throwMissingFirebaseDBError();
    }
  };

  pushToArray = async (path: string, values: any[]) => {
    if (this.firebaseAppStore.firebaseDb) {
      return await runTransaction(ref(this.firebaseAppStore.firebaseDb, path), (currentData: any[]) => {
        if (!currentData) {
          return values;
        }
        currentData.push(...values);
        return currentData;
      });
    } else {
      throwMissingFirebaseDBError();
    }
  };

  deleteFromArray = async (path: string, indices: number[]) => {
    if (this.firebaseAppStore.firebaseDb) {
      return await runTransaction(ref(this.firebaseAppStore.firebaseDb, path), (currentData: unknown[]) => {
        const newData: unknown[] = [];
        for (let i = 0; currentData !== null && i < currentData.length; i++) {
          if (!indices.includes(i)) {
            newData.push(currentData[i]);
          }
        }
        return newData;
      });
    } else {
      throwMissingFirebaseDBError();
    }
  };

  getArrayKey = (path: string) => {
    return push(ref(this.firebaseAppStore.firebaseDb!, path)).key;
  };
}
