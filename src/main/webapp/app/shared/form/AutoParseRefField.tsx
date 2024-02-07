import { ParsedRef, parseReferences } from 'app/oncokb-commons/components/RefComponent';
import React, { useEffect, useState } from 'react';
import _ from 'lodash';
import { onValue, ref } from '@firebase/database';
import { IRootStore } from 'app/stores';
import { componentInject } from '../util/typed-inject';
import { observer } from 'mobx-react';

interface IAutoParseRefField extends StoreProps {
  firebasePath: string;
}

/* eslint-disable @typescript-eslint/prefer-regexp-exec */
export const AutoParseRefField: React.FunctionComponent<IAutoParseRefField> = props => {
  const [summary, setSummary] = useState('');

  useEffect(() => {
    if (props.firebaseInitSuccess) {
      const unsubscribe = onValue(ref(props.firebaseDb, props.firebasePath), snapshot => {
        setSummary(snapshot.val() || '');
      });

      return () => {
        unsubscribe();
      };
    }
  }, [props.firebaseInitSuccess]);

  let content: Array<ParsedRef> = [];

  const regex = /(\(.*?[PMID|NCT|Abstract].*?\))/i;

  const parts = summary.split(regex);
  parts.forEach((part: string) => {
    if (part.match(regex)) {
      const parsedRef = parseReferences(part, true);
      parsedRef.filter(ref => ref.link).forEach(ref => content.push(ref));
    }
  });

  content = _.uniqBy(content, 'content');

  return content.length > 0 ? (
    <div className={'d-flex flex-wrap'}>
      <span>References:</span>
      {content.map(c => (
        <span className="ml-2" key={c.content}>
          <a target="_blank" rel="noopener noreferrer" href={c.link} style={{ whiteSpace: 'nowrap' }}>
            {`${c.prefix}${c.content}`}
          </a>
        </span>
      ))}
    </div>
  ) : (
    <></>
  );
};

const mapStoreToProps = ({ firebaseStore }: IRootStore) => ({
  firebaseDb: firebaseStore.firebaseDb,
  firebaseInitSuccess: firebaseStore.firebaseInitSuccess,
});

type StoreProps = Partial<ReturnType<typeof mapStoreToProps>>;

export default componentInject(mapStoreToProps)(observer(AutoParseRefField));
