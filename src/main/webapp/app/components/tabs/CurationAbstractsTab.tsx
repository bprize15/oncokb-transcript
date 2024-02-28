import { ParsedRef } from 'app/oncokb-commons/components/RefComponent';
import { IDrug } from 'app/shared/model/drug.model';
import { getFirebasePath, getMutationName, getTxName } from 'app/shared/util/firebase/firebase-utils';
import { componentInject } from 'app/shared/util/typed-inject';
import { getCancerTypesNameWithExclusion, parseTextForReferences } from 'app/shared/util/utils';
import { IRootStore } from 'app/stores';
import _ from 'lodash';
import React, { useEffect, useMemo, useState } from 'react';
import { Row, Input } from 'reactstrap';

export interface ICurationAbstractsTabProps extends StoreProps {
  hugoSymbol: string;
  drugList: IDrug[];
}

type AbstractData = {
  reference: ParsedRef;
  path: string;
};

/* eslint-disable no-console */
function CurationAbstractsTab({ hugoSymbol, geneData, addGeneListener, drugList }: ICurationAbstractsTabProps) {
  const firebaseGenePath = getFirebasePath('GENE', hugoSymbol);

  const abstracts = useMemo(() => {
    if (!geneData) {
      return [];
    }
    return findAbstracts(geneData);
  }, [geneData]);

  const [displayedAbstracts, setDisplayedAbstracts] = useState<AbstractData[]>(abstracts);

  function findAbstracts(obj, path = '') {
    let matches: AbstractData[] = [];

    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        const newPath = path ? `${path}, ${key}` : key;

        if (typeof obj[key] === 'string') {
          const references = parseTextForReferences(obj[key]);
          references.forEach(reference => matches.push({ reference, path: newPath }));
        } else if (typeof obj[key] === 'object' && key !== 'name_comments' && !key.endsWith('_review')) {
          matches = matches.concat(findAbstracts(obj[key], newPath));
        }
      }
    }

    return matches;
  }

  function parseLocationPath(path: string) {
    let mutationIndex = -1;
    let output = path.replace(/mutations, (\d)+/g, (match, index: string) => {
      mutationIndex = Number(index);
      return getMutationName(geneData.mutations[mutationIndex]);
    });

    let tumorIndex = -1;
    output = output.replace(/tumors, (\d)+/g, (match, index: string) => {
      tumorIndex = Number(index);
      const tumor = geneData.mutations[mutationIndex].tumors?.[tumorIndex];
      if (!tumor) {
        // look into how this is happening
        return match;
      }
      return getCancerTypesNameWithExclusion(tumor.cancerTypes, tumor.excludedCancerTypes || [], true);
    });

    output = output.replace(/TIs, (\d)+, treatments, (\d)+/g, (match, tiIndex, treatmentIndex) => {
      const treatmentName = geneData.mutations[mutationIndex].tumors?.[tumorIndex]?.TIs?.[tiIndex].treatments?.[treatmentIndex].name;
      if (!treatmentName) {
        return match;
      }
      return getTxName(drugList, treatmentName);
    });

    output = output.replace('background', 'Background');
    output = output.replace('mutation_effect', 'Mutation Effect');
    output = output.replace('description', 'Description of Evidence');
    output = output.replace('short', 'Additional Information');

    return output;
  }

  useEffect(() => {
    const callback = addGeneListener(firebaseGenePath);

    return () => {
      callback && callback();
    };
  }, [firebaseGenePath]);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
    const newValue = event.target.value;
    setDisplayedAbstracts(
      abstracts.filter(ref => `${ref.reference.prefix}${ref.reference.content}`.toLowerCase().includes(newValue.toLowerCase()))
    );
  }

  return (
    <div>
      <Row className="mb-3">
        <Input onChange={handleInputChange} placeholder="Enter abstract" />
      </Row>
      {displayedAbstracts.map(data => {
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

const mapStoreToProps = ({ firebaseGeneStore }: IRootStore) => ({
  geneData: firebaseGeneStore.data,
  addGeneListener: firebaseGeneStore.addListener,
});

type StoreProps = Partial<ReturnType<typeof mapStoreToProps>>;

export default componentInject(mapStoreToProps)(CurationAbstractsTab);
