import { ParsedRef } from 'app/oncokb-commons/components/RefComponent';
import { IDrug } from 'app/shared/model/drug.model';
import { Gene } from 'app/shared/model/firebase/firebase.model';
import { getFirebasePath, getMutationName, getTxName } from 'app/shared/util/firebase/firebase-utils';
import { componentInject } from 'app/shared/util/typed-inject';
import { getCancerTypesNameWithExclusion, parseTextForReferences } from 'app/shared/util/utils';
import { IRootStore } from 'app/stores';
import { DataSnapshot, onValue, ref } from 'firebase/database';
import _ from 'lodash';
import React, { useEffect, useMemo, useState } from 'react';
import { Row, Input } from 'reactstrap';

export interface ICurationAbstractsTabProps extends StoreProps {
  hugoSymbol: string;
  drugList: IDrug[];
}

type DisplayedReferenceData = {
  reference: ParsedRef;
  path: string;
  depth: number;
};

type ReferenceData = {
  [referenceName: string]: {
    minDepth: number;
    data: DisplayedReferenceData[];
  };
};

function CurationReferencesTab({ hugoSymbol, firebaseDb, drugList }: ICurationAbstractsTabProps) {
  const [inputValue, setInputValue] = useState('');
  const [displayedReferences, setDisplayedReferences] = useState<DisplayedReferenceData[]>([]);
  const [geneData, setGeneData] = useState<Gene>(null);

  const allReferences = useMemo(() => {
    if (!geneData) {
      return {};
    }
    const references: ReferenceData = {};
    findReferences(references, geneData);
    return references;
  }, [geneData]);

  function findReferences(references: ReferenceData, obj, path = '', depth = 0) {
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        const newPath = path ? `${path}, ${key}` : key;

        if (typeof obj[key] === 'string') {
          const newReferences = parseTextForReferences(obj[key]);
          newReferences.forEach(newReference => {
            const fullNewReferenceName = `${newReference.prefix}${newReference.content}`;

            if (references[fullNewReferenceName]) {
              references[fullNewReferenceName].data.push({ reference: newReference, path: newPath, depth });
            } else {
              references[fullNewReferenceName] = {
                minDepth: depth,
                data: [{ reference: newReference, path: newPath, depth }],
              };
            }
          });
        } else if (typeof obj[key] === 'object' && key !== 'name_comments' && !key.endsWith('_review')) {
          findReferences(references, obj[key], newPath, depth + 1);
        }
      }
    }
  }

  function parseLocationPath(path: string) {
    let mutationIndex = -1;
    let output = path.replace(/mutations, (\d+)/g, (match, index: string) => {
      mutationIndex = Number(index);
      return getMutationName(geneData.mutations[mutationIndex]);
    });

    let tumorIndex = -1;
    if (mutationIndex > -1) {
      output = output.replace(/tumors, (\d+)/g, (match, index: string) => {
        tumorIndex = Number(index);
        const tumor = geneData.mutations[mutationIndex].tumors[tumorIndex];
        return getCancerTypesNameWithExclusion(tumor.cancerTypes, tumor.excludedCancerTypes || [], true);
      });
    }

    if (tumorIndex > -1) {
      output = output.replace(/TIs, (\d+), treatments, (\d+)/g, (match, tiIndex, treatmentIndex) => {
        const treatmentName = geneData.mutations[mutationIndex].tumors[tumorIndex].TIs[tiIndex].treatments[treatmentIndex].name;
        return getTxName(drugList, treatmentName);
      });
    }

    output = output.replace('background', 'Background');
    output = output.replace('mutation_effect', 'Mutation Effect');
    output = output.replace('description', 'Description');
    output = output.replace('short', 'Additional Information');

    return output;
  }

  useEffect(() => {
    if (firebaseDb) {
      const firebaseGenePath = getFirebasePath('GENE', hugoSymbol);

      const callback = (snapshot: DataSnapshot) => {
        setGeneData(snapshot.val());
      };
      const unsubscribe = onValue(ref(firebaseDb, firebaseGenePath), callback);

      return () => {
        unsubscribe && unsubscribe();
      };
    }
  }, [firebaseDb]);

  useEffect(() => {
    const allReferenceKeys = Object.keys(allReferences);
    const newDisplayedReferenceKeys = allReferenceKeys.filter(refKey => refKey.toLowerCase().includes(inputValue.toLowerCase()));

    const newDisplayedReferencesValues = newDisplayedReferenceKeys.map(refKey => allReferences[refKey]);
    newDisplayedReferencesValues.sort((a, b) => a.minDepth - b.minDepth);

    const newDisplayedReferences = newDisplayedReferencesValues.reduce((accumulator: DisplayedReferenceData[], currentValue) => {
      const data = currentValue.data;
      data.sort((a, b) => a.depth - b.depth);
      return accumulator.concat(data);
    }, []);

    setDisplayedReferences(newDisplayedReferences);
  }, [allReferences, inputValue]);

  return (
    <div>
      <Row className="mb-3">
        <Input value={inputValue} onChange={event => setInputValue(event.target.value)} placeholder="Enter abstract" />
      </Row>
      {displayedReferences.map(data => {
        return (
          <div key={`${data.reference.content}_${data.path}`}>
            <Row className="mb-2">
              <span>
                <a target="_blank" rel="noopener noreferrer" href={data.reference.link}>
                  {`${data.reference.prefix}${data.reference.content}`}
                </a>
              </span>
            </Row>
            <Row className="border-bottom pb-3 mb-3">
              <span>
                <b>{`Location: ${parseLocationPath(data.path)}`}</b>
              </span>
            </Row>
          </div>
        );
      })}
    </div>
  );
}

const mapStoreToProps = ({ firebaseStore }: IRootStore) => ({
  firebaseDb: firebaseStore.firebaseDb,
});

type StoreProps = Partial<ReturnType<typeof mapStoreToProps>>;

export default componentInject(mapStoreToProps)(CurationReferencesTab);
