import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { IRootStore } from 'app/stores';
import { RouteComponentProps, useHistory } from 'react-router-dom';
import { getFirebaseGenePath, getFirebaseHistoryPath, getFirebaseMetaGenePath } from 'app/shared/util/firebase/firebase-utils';
import { Col, Row } from 'reactstrap';
import { GENE_TYPE, GENE_TYPE_KEY, INHERITANCE_MECHANISM_OPTIONS, READABLE_FIELD, PENETRANCE_OPTIONS } from 'app/config/constants/firebase';
import { GERMLINE_PATH, GET_ALL_DRUGS_PAGE_SIZE, PAGE_ROUTE, RADIO_OPTION_NONE } from 'app/config/constants/constants';
import CommentIcon from 'app/shared/icons/CommentIcon';
import GeneHistoryTooltip from 'app/components/geneHistoryTooltip/GeneHistoryTooltip';
import MutationsSection from './mutation/MutationsSection';
import OncoKBSidebar from 'app/components/sidebar/OncoKBSidebar';
import CurationHistoryTab from 'app/components/tabs/CurationHistoryTab';
import CurationToolsTab from 'app/components/tabs/CurationToolsTab';
import Tabs from 'app/components/tabs/tabs';
import { RealtimeCheckedInputGroup, RealtimeTextAreaInput } from 'app/shared/firebase/input/RealtimeInputs';
import GeneHeader from './header/GeneHeader';
import VusTable from 'app/shared/table/VusTable';
import * as styles from './styles.module.scss';
import CurationReferencesTab from 'app/components/tabs/CurationReferencesTab';
import GenomicIndicatorsTable from 'app/shared/table/GenomicIndicatorsTable';
import GeneRealtimeComponentHeader from './header/GeneRealtimeComponentHeader';
import RelevantCancerTypesModal from 'app/shared/modal/RelevantCancerTypesModal';
import { notifyError } from 'app/oncokb-commons/components/util/NotificationUtils';
import LoadingIndicator, { LoaderSize } from 'app/oncokb-commons/components/loadingIndicator/LoadingIndicator';
import { parseHistory } from 'app/shared/util/firebase/firebase-history-utils';
import { useMatchGeneEntity } from 'app/hooks/useMatchGeneEntity';
import { Unsubscribe, get, ref, onValue } from 'firebase/database';
import { getLocationIdentifier, getTooltipHistoryList } from 'app/components/geneHistoryTooltip/gene-history-tooltip-utils';
import GeneticTypeTabs, { GENETIC_TYPE } from './geneticTypeTabs/GeneticTypeTabs';
import GeneticTypeTabHeader from './header/GeneticTypeTabHeader';
import ReadOnlyBanner from './header/ReadOnlyBanner';
import FlagStore from 'app/entities/flag/flag.store';

export interface ICurationPageProps extends StoreProps, RouteComponentProps<{ hugoSymbol: string }> {}

export const CurationPage = (props: ICurationPageProps) => {
  const history = useHistory();
  const isGermline = props.isGermline;
  const hugoSymbolParam = props.match.params.hugoSymbol;

  const [firebaseGeneExists, setFirebaseGeneExists] = useState(false);
  const [mutationListRendered, setMutationListRendered] = useState(false);
  const mutationsSectionRef = useRef<HTMLDivElement>(null);

  const { geneEntity, hugoSymbol } = useMatchGeneEntity(hugoSymbolParam, props.searchGeneEntities, props.geneEntities);

  const firebaseGenePath = getFirebaseGenePath(isGermline, hugoSymbol);
  const firebaseHistoryPath = getFirebaseHistoryPath(isGermline, hugoSymbol);
  const mutationsPath = `${firebaseGenePath}/mutations`;
  const genomicIndicatorsPath = `${firebaseGenePath}/genomic_indicators`;
  const firebaseMetaGeneReviewPath = `${getFirebaseMetaGenePath(isGermline, hugoSymbol)}/review`;
  const firebaseMetaCurrentReviewerPath = `${firebaseMetaGeneReviewPath}/currentReviewer`;

  useEffect(() => {
    async function checkIfGeneExists() {
      if (props.firebaseDb && hugoSymbol) {
        const snapshot = await get(ref(props.firebaseDb, firebaseGenePath));
        if (!snapshot.exists()) {
          try {
            await props.createGene(hugoSymbol, isGermline);
          } catch (error) {
            notifyError(error);
            if (isGermline) {
              history.push(PAGE_ROUTE.CURATION_GERMLINE);
            } else {
              history.push(PAGE_ROUTE.CURATION_SOMATIC);
            }
          }
        }
        setFirebaseGeneExists(true);
      }
    }

    checkIfGeneExists();
  }, [firebaseGenePath, setFirebaseGeneExists, props.firebaseDb, hugoSymbol, isGermline]);

  useEffect(() => {
    props.getDrugs({ page: 0, size: GET_ALL_DRUGS_PAGE_SIZE, sort: ['id,asc'] });
    return () => {
      props.setOpenMutationCollapsibleIndex(null);
    };
  }, []);

  useEffect(() => {
    if (!props.firebaseDb) {
      return;
    }
    if (geneEntity && props.firebaseInitSuccess) {
      const cleanupCallbacks: Unsubscribe[] = [];
      cleanupCallbacks.push(
        onValue(ref(props.firebaseDb, firebaseMetaCurrentReviewerPath), snapshot => {
          props.setReadOnly(!!snapshot.val());
        }),
      );
      cleanupCallbacks.push(props.addHistoryListener(firebaseHistoryPath));
      cleanupCallbacks.push(props.addMutationListListener(mutationsPath));
      return () => {
        cleanupCallbacks.forEach(callback => callback && callback());
      };
    }
  }, [geneEntity, props.firebaseInitSuccess, props.firebaseDb, firebaseGenePath, firebaseHistoryPath, firebaseMetaCurrentReviewerPath]);

  const tabHistoryList = useMemo(() => {
    if (!props.historyData) {
      return;
    }

    return parseHistory(props.historyData, props.drugList);
  }, [props.historyData]);

  const tooltipHistoryList = useMemo(() => {
    if (!tabHistoryList) {
      return;
    }

    return getTooltipHistoryList(tabHistoryList);
  }, [tabHistoryList]);

  return props.firebaseInitSuccess && !props.loadingGenes && !!geneEntity && firebaseGeneExists && hugoSymbol ? (
    <>
      <div style={{ visibility: mutationListRendered ? 'visible' : 'hidden' }}>
        <GeneHeader firebaseGenePath={firebaseGenePath} geneEntity={geneEntity} isReviewing={false} />
        <GeneticTypeTabs geneEntity={geneEntity} geneticType={isGermline ? GENETIC_TYPE.GERMLINE : GENETIC_TYPE.SOMATIC} />
        <div className="d-flex justify-content-end mt-2 mb-2">
          <GeneticTypeTabHeader hugoSymbol={hugoSymbol} isReviewing={false} />
        </div>
        {props.readOnly && <ReadOnlyBanner hugoSymbol={hugoSymbol} />}
        <div className="mb-4">
          <Row className={'justify-content-between'}>
            <Col className="pb-2">
              <RealtimeCheckedInputGroup
                disabled={props.readOnly}
                groupHeader={
                  <>
                    <span className="me-2">Gene Type</span>
                    {
                      <GeneHistoryTooltip
                        historyData={tooltipHistoryList}
                        location={READABLE_FIELD.GENE_TYPE}
                        locationIdentifier={getLocationIdentifier({ fields: [READABLE_FIELD.GENE_TYPE] })}
                      />
                    }
                  </>
                }
                options={[GENE_TYPE.TUMOR_SUPPRESSOR, GENE_TYPE.ONCOGENE].map(label => {
                  return {
                    label,
                    firebasePath: `${firebaseGenePath}/${GENE_TYPE_KEY[label]}`,
                  };
                })}
              />
              <RealtimeTextAreaInput
                disabled={props.readOnly}
                firebasePath={`${firebaseGenePath}/summary`}
                inputClass={styles.textarea}
                label="Gene Summary"
                name="geneSummary"
                labelIcon={
                  <GeneRealtimeComponentHeader
                    tooltip={
                      <GeneHistoryTooltip
                        historyData={tooltipHistoryList}
                        location={READABLE_FIELD.SUMMARY}
                        locationIdentifier={getLocationIdentifier({ fields: [READABLE_FIELD.SUMMARY] })}
                      />
                    }
                    commentIcon={<CommentIcon id={`${hugoSymbol}_gene_summary`} path={`${firebaseGenePath}/summary_comments`} />}
                  />
                }
              />
            </Col>
          </Row>
          <Row className="mb-3">
            <Col>
              <RealtimeTextAreaInput
                disabled={props.readOnly}
                firebasePath={`${firebaseGenePath}/background`}
                inputClass={styles.textarea}
                label="Background"
                name="geneBackground"
                parseRefs
                labelIcon={
                  <GeneRealtimeComponentHeader
                    tooltip={
                      <GeneHistoryTooltip
                        historyData={tooltipHistoryList}
                        location={READABLE_FIELD.BACKGROUND}
                        locationIdentifier={getLocationIdentifier({ fields: [READABLE_FIELD.BACKGROUND] })}
                      />
                    }
                    commentIcon={<CommentIcon id={`${hugoSymbol}_gene_background`} path={`${firebaseGenePath}/background_comments`} />}
                  />
                }
              />
            </Col>
          </Row>
          {isGermline && (
            <>
              <div className="mb-3">
                <RealtimeCheckedInputGroup
                  disabled={props.readOnly}
                  groupHeader={
                    <GeneRealtimeComponentHeader
                      title="Penetrance"
                      tooltip={
                        <GeneHistoryTooltip
                          historyData={tooltipHistoryList}
                          location={READABLE_FIELD.PENETRANCE}
                          locationIdentifier={getLocationIdentifier({ fields: [READABLE_FIELD.PENETRANCE] })}
                        />
                      }
                      commentIcon={<CommentIcon id={`${hugoSymbol}_penetrance`} path={`${firebaseGenePath}/penetrance_comments`} />}
                    />
                  }
                  isRadio
                  options={[...PENETRANCE_OPTIONS, RADIO_OPTION_NONE].map(label => ({
                    label,
                    firebasePath: `${firebaseGenePath}/penetrance`,
                  }))}
                />
              </div>
              <div className="mb-3">
                <RealtimeCheckedInputGroup
                  disabled={props.readOnly}
                  groupHeader={
                    <GeneRealtimeComponentHeader
                      title="Mechanism of Inheritance"
                      tooltip={
                        <GeneHistoryTooltip
                          historyData={tooltipHistoryList}
                          location={READABLE_FIELD.INHERITANCE_MECHANISM}
                          locationIdentifier={getLocationIdentifier({ fields: [READABLE_FIELD.INHERITANCE_MECHANISM] })}
                        />
                      }
                      commentIcon={
                        <CommentIcon id={`${hugoSymbol}_inheritanceMechanism`} path={`${firebaseGenePath}/inheritanceMechanism_comments`} />
                      }
                    />
                  }
                  isRadio
                  options={[...INHERITANCE_MECHANISM_OPTIONS, RADIO_OPTION_NONE].map(label => ({
                    label,
                    firebasePath: `${firebaseGenePath}/inheritanceMechanism`,
                  }))}
                />
              </div>
              <GenomicIndicatorsTable genomicIndicatorsPath={genomicIndicatorsPath} />
            </>
          )}
        </div>
        <div ref={mutationsSectionRef}>
          <MutationsSection
            mutationsPath={mutationsPath}
            metaGeneReviewPath={firebaseMetaGeneReviewPath}
            hugoSymbol={hugoSymbol ?? ''}
            isGermline={isGermline}
            parsedHistoryList={tooltipHistoryList ?? new Map()}
            onMutationListRender={() => setMutationListRendered(true)}
          />
        </div>
        <VusTable hugoSymbol={hugoSymbol} isGermline={isGermline} mutationsSectionRef={mutationsSectionRef} />
        <RelevantCancerTypesModal
          onConfirm={async (newExcludedRCTs, noneDeleted) => {
            try {
              const newRCTs = noneDeleted ? [] : newExcludedRCTs;
              await props.updateRelevantCancerTypes(
                props.relevantCancerTypesModalStore.pathToRelevantCancerTypes ?? '',
                noneDeleted ? [] : props.relevantCancerTypesModalStore.firebaseExcludedRCTs ?? [],
                newRCTs,
                props.relevantCancerTypesModalStore.excludedRCTsReview!,
                props.relevantCancerTypesModalStore.excludedRCTsUuid!,
                isGermline,
                props.relevantCancerTypesModalStore.firebaseExcludedRCTs === undefined,
              );
              props.relevantCancerTypesModalStore.closeModal();
            } catch (error) {
              notifyError(error);
            }
          }}
          onCancel={() => props.relevantCancerTypesModalStore.closeModal()}
        />
        <OncoKBSidebar>
          <Tabs
            className="pe-4 ps-2 mt-1"
            tabs={[
              {
                title: 'Tools',
                content: <CurationToolsTab genePath={firebaseGenePath} isGermline={isGermline} hugoSymbol={hugoSymbol ?? ''} />,
              },
              {
                title: 'History',
                content: <CurationHistoryTab historyData={tabHistoryList} />,
              },
              {
                title: 'References',
                content: <CurationReferencesTab genePath={firebaseGenePath} />,
              },
            ]}
          />
        </OncoKBSidebar>
      </div>
      {!mutationListRendered && <LoadingIndicator key={'curation-page-loading'} size={LoaderSize.LARGE} center isLoading />}
    </>
  ) : (
    <LoadingIndicator key={'curation-page-loading'} size={LoaderSize.LARGE} center isLoading />
  );
};

const mapStoreToProps = ({
  geneStore,
  firebaseAppStore,
  firebaseHistoryStore,
  firebaseMutationListStore,
  drugStore,
  relevantCancerTypesModalStore,
  authStore,
  firebaseGeneService,
  openMutationCollapsibleStore,
  layoutStore,
  routerStore,
  curationPageStore,
}: IRootStore) => ({
  firebaseDb: firebaseAppStore.firebaseDb,
  firebaseInitSuccess: firebaseAppStore.firebaseInitSuccess,
  searchGeneEntities: geneStore.searchEntities,
  geneEntities: geneStore.entities,
  loadingGenes: geneStore.loading,
  historyData: firebaseHistoryStore.data,
  addHistoryListener: firebaseHistoryStore.addListener,
  addMutationListListener: firebaseMutationListStore.addListener,
  drugList: drugStore.entities,
  getDrugs: drugStore.getEntities,
  relevantCancerTypesModalStore,
  fullName: authStore.fullName,
  updateRelevantCancerTypes: firebaseGeneService.updateRelevantCancerTypes,
  createGene: firebaseGeneService.createGene,
  setOpenMutationCollapsibleIndex: openMutationCollapsibleStore.setOpenMutationCollapsibleIndex,
  toggleOncoKBSidebar: layoutStore.toggleOncoKBSidebar,
  isGermline: routerStore.isGermline,
  readOnly: curationPageStore.readOnly,
  setReadOnly: curationPageStore.setReadOnly,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(CurationPage);
