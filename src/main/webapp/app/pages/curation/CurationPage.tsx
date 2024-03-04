import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { RouteComponentProps, useHistory } from 'react-router-dom';
import { Button, Col, Container, Input, InputGroup, Label, Modal, ModalBody, ModalHeader, Row } from 'reactstrap';
import { ViewportList } from 'react-viewport-list';
import { IRootStore } from 'app/stores';
import LoadingIndicator, { LoaderSize } from 'app/oncokb-commons/components/loadingIndicator/LoadingIndicator';
import {
  CBIOPORTAL,
  CHECKBOX_LABEL_LEFT_MARGIN,
  COSMIC,
  GET_ALL_DRUGS_PAGE_SIZE,
  PAGE_ROUTE,
  PENETRANCE,
  RADIO_OPTION_NONE,
} from 'app/config/constants/constants';
import { PubmedGeneLink } from 'app/shared/links/PubmedGeneLink';
import { InlineDivider } from 'app/shared/links/PubmedGeneArticlesLink';
import { getSectionClassName, getUserFullName } from 'app/shared/util/utils';
import ExternalLinkIcon from 'app/shared/icons/ExternalLinkIcon';
import WithSeparator from 'react-with-separator';
import { AutoParseRefField } from 'app/shared/form/AutoParseRefField';
import { RealtimeCheckedInputGroup, RealtimeTextAreaInput } from 'app/shared/firebase/input/FirebaseRealtimeInput';
import { geneNeedsReview, getFirebasePath, getMutationName } from 'app/shared/util/firebase/firebase-utils';
import styles from './styles.module.scss';
import { notifyError } from 'app/oncokb-commons/components/util/NotificationUtils';
import { Comment, HistoryRecord, Mutation } from 'app/shared/model/firebase/firebase.model';
import { GENE_TYPE, GENE_TYPE_KEY, MUTATION_EFFECT_OPTIONS, ONCOGENICITY_OPTIONS, TX_LEVEL_OPTIONS } from 'app/config/constants/firebase';
import GeneHistoryTooltip from 'app/components/geneHistoryTooltip/GeneHistoryTooltip';
import VusTable from '../../shared/table/VusTable';
import OncoKBSidebar from 'app/components/sidebar/OncoKBSidebar';
import Tabs from 'app/components/tabs/tabs';
import CurationHistoryTab from 'app/components/tabs/CurationHistoryTab';
import { FaFilter } from 'react-icons/fa';
import _ from 'lodash';
import MutationCollapsible from './collapsible/MutationCollapsible';
import { IDrug } from 'app/shared/model/drug.model';
import { IGene } from 'app/shared/model/gene.model';
import CurationToolsTab from 'app/components/tabs/CurationToolsTab';
import CommentIcon from 'app/shared/icons/CommentIcon';
import { HgncLink } from 'app/shared/links/HgncLink';
import ReviewPage from './review/ReviewPage';
import AddMutationModal from 'app/shared/modal/AddMutationModal';
import AddMutationButton from './button/AddMutationButton';
import Mutations from 'app/pages/curation/Mutations';
import CurationReferencesTab from 'app/components/tabs/CurationReferencesTab';

export interface ICurationPageProps extends StoreProps, RouteComponentProps<{ hugoSymbol: string }> {}

export type ParsedHistoryRecord = { record: HistoryRecord; timestamp: number; admin: string };

export type FirebaseMutation = Mutation & {
  firebaseIndex: number;
};

const CurationPage = (props: ICurationPageProps) => {
  const history = useHistory();
  const hugoSymbol = props.match.params.hugoSymbol.toUpperCase();
  const firebaseGenePath = getFirebasePath('GENE', hugoSymbol);
  const firebaseHistoryPath = getFirebasePath('HISTORY', hugoSymbol);
  const firebaseMetaPath = getFirebasePath('META_GENE', hugoSymbol);

  const [isReviewing, setIsReviewing] = useState(false);
  const [isReviewFinished, setIsReviewFinished] = useState(false);

  const [showAddMutationModal, setShowAddMutationModal] = useState(false);
  const [mutationFilter, setMutationFilter] = useState('');

  const [showFilterModal, setShowFilterModal] = useState(false);
  const [mutations, setMutations] = useState<FirebaseMutation[]>([]);

  const [oncogenicityFilter, setOncogenicityFilter] = useState(initFilterCheckboxState(ONCOGENICITY_OPTIONS));
  const [tempOncogenicityFilter, setTempOncogenicityFilter] = useState(initFilterCheckboxState(ONCOGENICITY_OPTIONS));

  const [mutationEffectFilter, setMutationEffectFilter] = useState(initFilterCheckboxState(MUTATION_EFFECT_OPTIONS));
  const [tempMutationEffectFilter, setTempMutationEffectFilter] = useState(initFilterCheckboxState(MUTATION_EFFECT_OPTIONS));

  const [txLevelFilter, setTxLevelFilter] = useState(initFilterCheckboxState(TX_LEVEL_OPTIONS));
  const [tempTxLevelFilter, setTempTxLevelFilter] = useState(initFilterCheckboxState(TX_LEVEL_OPTIONS));

  const [enabledCheckboxes, setEnabledCheckboxes] = useState<string[]>([]);

  const [drugList, setDrugList] = useState<IDrug[]>([]);

  const [openMutationCollapsible, setOpenMutationCollapsible] = useState<Mutation>(null);
  const [mutationCollapsibleScrollIndex, setMutationCollapsibleScrollIndex] = useState(0);

  function initFilterCheckboxState(options: string[]) {
    return options.map(option => ({ label: option, selected: false, disabled: false }));
  }

  const isGeneCurated = useMemo(() => {
    if (props.metaListData) {
      return Object.keys(props.metaListData).includes(hugoSymbol);
    }
    return true;
  }, [props.metaListData]);

  const mutationsAreFiltered = useMemo(() => {
    return (
      oncogenicityFilter.some(filter => filter.selected) ||
      mutationEffectFilter.some(filter => filter.selected) ||
      txLevelFilter.some(filter => filter.selected) ||
      mutationFilter
    );
  }, [oncogenicityFilter, mutationEffectFilter, txLevelFilter, mutationFilter]);

  const showFilterModalCancelButton = useMemo(() => {
    return (
      tempOncogenicityFilter.some(filter => filter.selected) ||
      tempMutationEffectFilter.some(filter => filter.selected) ||
      tempTxLevelFilter.some(filter => filter.selected)
    );
  }, [tempOncogenicityFilter, tempMutationEffectFilter, tempTxLevelFilter]);

  function handleToggleFilterModal() {
    setShowFilterModal(showModal => !showModal);
  }

  function handleFilterCheckboxChange(
    index: number,
    setState: React.Dispatch<
      React.SetStateAction<
        {
          label: string;
          selected: boolean;
        }[]
      >
    >
  ) {
    setState(currentState =>
      currentState.map((filter, filterIndex) => {
        if (index === filterIndex) {
          return { label: filter.label, selected: !filter.selected };
        }
        return filter;
      })
    );
  }

  async function handleCreateComment(path: string, content: string, currentCommentsLength: number) {
    // replace with runTransaction?
    const newComment = new Comment();
    newComment.content = content;
    newComment.email = props.account.email;
    newComment.resolved = 'false';
    newComment.userName = getUserFullName(props.account);

    try {
      await props.handleFirebaseUpdateUntemplated(path, [...Array(currentCommentsLength).fill({}), newComment]);
    } catch (error) {
      notifyError(error);
    }
  }

  async function handleDeleteComments(path: string, indices: number[]) {
    try {
      await props.handleFirebaseDeleteFromArray(path, indices);
    } catch (error) {
      notifyError(error);
    }
  }

  async function handleResolveComment(path: string) {
    try {
      await props.handleFirebaseUpdateUntemplated(path, { resolved: true });
    } catch (error) {
      notifyError(error);
    }
  }

  async function handleUnresolveComment(path: string) {
    try {
      await props.handleFirebaseUpdateUntemplated(path, { resolved: false });
    } catch (error) {
      notifyError(error);
    }
  }

  useEffect(() => {
    if (props.firebaseInitSuccess) {
      props.searchGeneEntities({ query: hugoSymbol, exact: true });
      const cleanupCallbacks = [];
      cleanupCallbacks.push(props.addListener(firebaseGenePath));
      cleanupCallbacks.push(props.addHistoryListener(firebaseHistoryPath));
      // cleanupCallbacks.push(props.addMetaListener(firebaseMetaPath));
      // cleanupCallbacks.push(props.addMetaListListener());
      cleanupCallbacks.push(() => props.updateCollaborator(hugoSymbol, false));
      cleanupCallbacks.push(props.addMetaCollaboratorsListener());
      return () => {
        cleanupCallbacks.forEach(callback => callback && callback());
      };
    }
  }, [props.firebaseInitSuccess]);

  useEffect(() => {
    if (props.metaData) {
      const currentReviewer = props.metaData?.review?.currentReviewer;
      setIsReviewing(currentReviewer?.toLowerCase() === props.fullName.toLowerCase());
    }
  }, [props.metaData]);

  const geneEntity: IGene | undefined = useMemo(() => {
    return props.geneEntities.find(gene => gene.hugoSymbol === hugoSymbol);
  }, [props.geneEntities]);

  useEffect(() => {
    if (props.metaCollaboratorsData && props.data?.name) {
      props.updateCollaborator(hugoSymbol, true).catch(error => {
        notifyError(error);
        history.push(PAGE_ROUTE.CURATION);
      });
    }
  }, [props.metaCollaboratorsData, props.data]);

  useEffect(() => {
    filterMutations();
  }, [props.data, mutationFilter, oncogenicityFilter, mutationEffectFilter, txLevelFilter]);

  useEffect(() => {
    if (props.mutationSummaryStats) {
      const allMutationSummaries = Object.values(props.mutationSummaryStats);

      const allOncogenicities = new Set(allMutationSummaries.map(summary => summary.oncogenicity));
      const allMutationEffects = new Set(allMutationSummaries.map(summary => summary.mutationEffect));
      const allTxLevels = new Set(_.flatten(allMutationSummaries.map(summary => Object.keys(summary.txLevels))));

      setEnabledCheckboxes([...allOncogenicities, ...allMutationEffects, ...allTxLevels]);
    }
  }, [props.mutationSummaryStats]);

  useEffect(() => {
    async function fetchAllDrugs() {
      const drugs = await props.getDrugs({ page: 0, size: GET_ALL_DRUGS_PAGE_SIZE, sort: 'id,asc' });
      setDrugList(drugs['data']);
    }

    fetchAllDrugs();
  }, []);

  const parsedHistoryList = useMemo(() => {
    if (!props.historyData) {
      return;
    }

    const newList = new Map<string, ParsedHistoryRecord[]>();

    for (const historyData of Object.values(props.historyData)) {
      try {
        for (const record of historyData.records) {
          if (!newList.has(record.location)) {
            newList.set(record.location, []);
          }
          newList.get(record.location).push({ record, timestamp: historyData.timeStamp, admin: historyData.admin });
        }
      } catch {
        continue;
      }
    }

    return newList;
  }, [props.historyData]);

  function filterMutations() {
    setMutations(
      (props.data?.mutations || []).reduce<FirebaseMutation[]>((filteredMutations, mutation, index) => {
        const matchesName = !mutationFilter || getMutationName(mutation).toLowerCase().includes(mutationFilter.toLowerCase());

        const selectedOncogenicities = oncogenicityFilter.filter(filter => filter.selected);
        const matchesOncogenicity =
          selectedOncogenicities.length === 0 ||
          selectedOncogenicities.some(oncogenicity => oncogenicity.label === mutation.mutation_effect.oncogenic);

        const selectedMutationEffects = mutationEffectFilter.filter(filter => filter.selected);
        const matchesMutationEffect =
          selectedMutationEffects.length === 0 ||
          selectedMutationEffects.some(mutationEffect => mutationEffect.label === mutation.mutation_effect.effect);

        function matchesTxLevel() {
          const selectedTxLevels = txLevelFilter.filter(txLevel => txLevel.selected);
          if (selectedTxLevels.length === 0) {
            return true;
          }

          if (!mutation.tumors) {
            return false;
          }

          for (const tumor of mutation.tumors) {
            for (const TI of tumor.TIs) {
              if (!TI.treatments) {
                continue;
              }

              for (const treatment of TI.treatments) {
                if (selectedTxLevels.some(txLevel => txLevel.label === treatment.level)) {
                  return true;
                }
              }
            }
          }
          return false;
        }

        if (matchesName && matchesOncogenicity && matchesMutationEffect && matchesTxLevel()) {
          return [...filteredMutations, { ...mutation, firebaseIndex: index }];
        }
        return filteredMutations;
      }, []) || []
    );
  }
  const handleReviewButtonClick = () => {
    if (isReviewing) {
      setIsReviewFinished(false);
    }
    props.updateMeta(`${firebaseMetaPath}`, { review: { currentReviewer: isReviewing ? '' : props.fullName } });
  };

  const handleReviewFinished = (isFinished: boolean) => {
    setIsReviewFinished(isFinished);
  };

  const getReviewButtons = () => {
    let button;
    if (geneNeedsReview(props.metaData)) {
      if (isReviewing || isReviewFinished) {
        button = (
          <Button color="primary" onClick={handleReviewButtonClick}>
            Review Complete
          </Button>
        );
      } else {
        button = (
          <Button outline color="primary" onClick={handleReviewButtonClick}>
            Review
          </Button>
        );
      }
    } else {
      if (isReviewFinished) {
        button = (
          <Button color="primary" onClick={handleReviewButtonClick}>
            Review Complete
          </Button>
        );
      } else {
        return undefined;
      }
    }
    return <>{button}</>;
  };

  console.log('CurationPage rendering');
  return !!props.data && drugList.length > 0 && !props.loadingGenes ? (
    <>
      <div>
        <Row className={'mb-2'}>
          <Col className={'d-flex justify-content-between flex-row flex-nowrap align-items-end'}>
            <div className="d-flex align-items-end all-children-margin">
              <span style={{ fontSize: '3rem', lineHeight: 1 }} className={'mr-2'}>
                {props.data.name}
              </span>
              {!isReviewing && (
                <>
                  <CommentIcon
                    id={`${hugoSymbol}_curation_page`}
                    comments={props.data.name_comments || []}
                    onCreateComment={content =>
                      handleCreateComment(`${firebaseGenePath}/name_comments`, content, props.data.name_comments?.length || 0)
                    }
                    onDeleteComments={indices => handleDeleteComments(`${firebaseGenePath}/name_comments`, indices)}
                    onResolveComment={index => handleResolveComment(`${firebaseGenePath}/name_comments/${index}`)}
                    onUnresolveComment={index => handleUnresolveComment(`${firebaseGenePath}/name_comments/${index}`)}
                  />
                  <div>
                    <span>
                      {geneEntity?.entrezGeneId && (
                        <span className="ml-2">
                          <span className="font-weight-bold text-nowrap">Entrez Gene:</span>
                          <span className="ml-1">
                            <PubmedGeneLink entrezGeneId={geneEntity.entrezGeneId} />
                          </span>
                        </span>
                      )}
                      {geneEntity?.hgncId && (
                        <span className="ml-2">
                          <span className="font-weight-bold">HGNC:</span>
                          <span className="ml-1">
                            <HgncLink id={geneEntity.hgncId} />
                          </span>
                        </span>
                      )}
                      {geneEntity?.synonyms && geneEntity.synonyms.length > 0 && (
                        <span className="ml-2">
                          <span className="font-weight-bold">Gene aliases:</span>
                          <span className="ml-1">
                            <WithSeparator separator={', '}>
                              {geneEntity.synonyms.map(synonym => (
                                <span className={'text-nowrap'} key={synonym.name}>
                                  {synonym.name}
                                </span>
                              ))}
                            </WithSeparator>
                          </span>
                        </span>
                      )}
                      <span className="ml-2">
                        <span className="font-weight-bold mr-2">External Links:</span>
                        <WithSeparator separator={InlineDivider}>
                          <a href={`https://cbioportal.mskcc.org/ln?q=${props.data.name}`} target="_blank" rel="noopener noreferrer">
                            {CBIOPORTAL} <ExternalLinkIcon />
                          </a>
                          <a
                            href={`http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=${props.data.name}`}
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {COSMIC} <ExternalLinkIcon />
                          </a>
                        </WithSeparator>
                      </span>
                    </span>
                  </div>
                </>
              )}
            </div>
            {getReviewButtons()}
          </Col>
        </Row>
        {isReviewing ? (
          <ReviewPage
            hugoSymbol={hugoSymbol}
            firebasePath={firebaseGenePath}
            reviewFinished={isReviewFinished}
            handleReviewFinished={handleReviewFinished}
            drugList={drugList}
          />
        ) : (
          <>
            <Row className={`${getSectionClassName()} justify-content-between`}>
              <Col>
                <RealtimeCheckedInputGroup
                  groupHeader={
                    <>
                      <span className="mr-2">Gene Type</span>
                      {<GeneHistoryTooltip historyData={parsedHistoryList} location={'Gene Type'} />}
                    </>
                  }
                  options={[GENE_TYPE.TUMOR_SUPPRESSOR, GENE_TYPE.ONCOGENE].map(label => {
                    return {
                      label,
                      fieldKey: GENE_TYPE_KEY[label],
                    };
                  })}
                />
                <RealtimeTextAreaInput
                  fieldKey="summary"
                  label="Somatic Gene Summary "
                  labelIcon={
                    <>
                      <GeneHistoryTooltip historyData={parsedHistoryList} location={'Gene Summary'} />
                      <div className="mr-3" />
                      <CommentIcon
                        id={props.data.summary_uuid}
                        comments={props.data.summary_comments || []}
                        onCreateComment={content =>
                          handleCreateComment(`${firebaseGenePath}/summary_comments`, content, props.data.summary_comments?.length || 0)
                        }
                        onDeleteComments={indices => handleDeleteComments(`${firebaseGenePath}/summary_comments`, indices)}
                        onResolveComment={index => handleResolveComment(`${firebaseGenePath}/summary_comments/${index}`)}
                        onUnresolveComment={index => handleUnresolveComment(`${firebaseGenePath}/summary_comments/${index}`)}
                      />
                    </>
                  }
                />
                <RealtimeTextAreaInput
                  fieldKey="germline_summary"
                  label="Germline Gene Summary"
                  labelIcon={
                    <>
                      <GeneHistoryTooltip historyData={parsedHistoryList} location={'Germline Gene Summary'} />
                      <div className="mr-3" />
                      <CommentIcon
                        id={props.data.germline_summary_uuid}
                        comments={props.data.germline_summary_comments || []}
                        onCreateComment={content =>
                          handleCreateComment(
                            `${firebaseGenePath}/germline_summary_comments`,
                            content,
                            props.data.germline_summary_comments?.length || 0
                          )
                        }
                        onDeleteComments={indices => handleDeleteComments(`${firebaseGenePath}/germline_summary_comments`, indices)}
                        onResolveComment={index => handleResolveComment(`${firebaseGenePath}/germline_summary_comments/${index}`)}
                        onUnresolveComment={index => handleUnresolveComment(`${firebaseGenePath}/germline_summary_comments/${index}`)}
                      />
                    </>
                  }
                />
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <RealtimeCheckedInputGroup
                  groupHeader="Penetrance"
                  isRadio
                  options={[PENETRANCE.HIGH, PENETRANCE.INTERMEDIATE, PENETRANCE.LOW, PENETRANCE.OTHER, RADIO_OPTION_NONE].map(label => {
                    return {
                      label,
                      fieldKey: 'penetrance',
                    };
                  })}
                />
              </Col>
            </Row>
            <Row className={'mb-5'}>
              <Col>
                <RealtimeTextAreaInput
                  fieldKey="background"
                  inputClass={styles.textarea}
                  label="Background"
                  name="geneBackground"
                  labelIcon={
                    <>
                      <GeneHistoryTooltip historyData={parsedHistoryList} location={'Gene Background'} />
                      <div className="mr-3" />
                      <CommentIcon
                        id={props.data.background_uuid}
                        comments={props.data.background_comments || []}
                        onCreateComment={content =>
                          handleCreateComment(
                            `${firebaseGenePath}/background_comments`,
                            content,
                            props.data.background_comments?.length || 0
                          )
                        }
                        onDeleteComments={indices => handleDeleteComments(`${firebaseGenePath}/background_comments`, indices)}
                        onResolveComment={index => handleResolveComment(`${firebaseGenePath}/background_comments/${index}`)}
                        onUnresolveComment={index => handleUnresolveComment(`${firebaseGenePath}/background_comments/${index}`)}
                      />
                    </>
                  }
                />
                <div className="mb-2">
                  <AutoParseRefField summary={props.data.background} />
                </div>
              </Col>
            </Row>
            <Mutations
              db={props.db}
              fieldKey={getFirebasePath('GENE', hugoSymbol) + '/mutations'}
              drugList={drugList}
              historyData={parsedHistoryList}
            />
            <VusTable hugoSymbol={hugoSymbol} />
          </>
        )}
      </div>
      <OncoKBSidebar>
        <Tabs
          tabs={[
            {
              title: 'Tools',
              content: <CurationToolsTab />,
            },
            {
              title: 'History',
              content: <CurationHistoryTab historyData={props.historyData} />,
            },
            {
              title: 'References',
              content: <CurationReferencesTab hugoSymbol={hugoSymbol} drugList={drugList} />,
            },
          ]}
        />
      </OncoKBSidebar>
    </>
  ) : (
    <LoadingIndicator size={LoaderSize.LARGE} center={true} isLoading />
  );
};

const mapStoreToProps = ({
  geneStore,
  firebaseGeneStore,
  firebaseMetaStore,
  firebaseHistoryStore,
  drugStore,
  authStore,
  firebaseStore,
}: IRootStore) => ({
  db: firebaseGeneStore.db,
  searchGeneEntities: geneStore.searchEntities,
  geneEntities: geneStore.entities,
  loadingGenes: geneStore.loading,
  addListener: firebaseGeneStore.addListener,
  data: firebaseGeneStore.data,
  update: firebaseGeneStore.update,
  updateMutations: firebaseGeneStore.pushToArrayFront,
  updateReviewableContent: firebaseGeneStore.updateReviewableContent,
  deleteSection: firebaseGeneStore.deleteSection,
  mutationSummaryStats: firebaseGeneStore.mutationLevelMutationSummaryStats,
  addMetaListener: firebaseMetaStore.addListener,
  addMetaCollaboratorsListener: firebaseMetaStore.addMetaCollaboratorsListener,
  addMetaListListener: firebaseMetaStore.addMetaListListener,
  metaData: firebaseMetaStore.data,
  metaListData: firebaseMetaStore.metaList,
  getDrugs: drugStore.getEntities,
  metaCollaboratorsData: firebaseMetaStore.metaCollaborators,
  updateCollaborator: firebaseMetaStore.updateCollaborator,
  updateMeta: firebaseMetaStore.update,
  historyData: firebaseHistoryStore.data,
  addHistoryListener: firebaseHistoryStore.addListener,
  handleFirebaseUpdateUntemplated: firebaseGeneStore.updateUntemplated,
  handleFirebaseDeleteFromArray: firebaseGeneStore.deleteFromArray,
  account: authStore.account,
  firebaseInitSuccess: firebaseStore.firebaseInitSuccess,
  fullName: authStore.fullName,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(CurationPage);
