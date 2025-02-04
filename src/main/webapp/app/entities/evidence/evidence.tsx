import React from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { RouteComponentProps } from 'react-router-dom';

import { IEvidence } from 'app/shared/model/evidence.model';
import { ENTITY_ACTION, ENTITY_TYPE } from 'app/config/constants/constants';

import { IRootStore } from 'app/stores';
import OncoKBAsyncTable, { PaginationState } from 'app/shared/table/OncoKBAsyncTable';
import { getEntityTableActionsColumn, getPaginationFromSearchParams } from 'app/shared/util/utils';
import EntityActionButton from 'app/shared/button/EntityActionButton';
import { Column } from 'react-table';

const defaultPaginationState: PaginationState<IEvidence> = {
  activePage: 1,
  order: 'asc',
  sort: 'id',
};

export interface IEvidenceProps extends StoreProps, RouteComponentProps<{ url: string }> {}

export const Evidence = (props: IEvidenceProps) => {
  const columns: Column<IEvidence>[] = [
    { accessor: 'uuid', Header: 'UUID' },
    { accessor: 'evidenceType', Header: 'Evidence Type' },
    { accessor: 'knownEffect', Header: 'Known Effect' },
    { accessor: 'gene.hugoSymbol', Header: 'Gene' },
    getEntityTableActionsColumn(ENTITY_TYPE.EVIDENCE),
  ];

  return (
    <div>
      <h2 id="evidence-heading" data-cy="EvidenceHeading">
        Evidences
        <EntityActionButton className="ms-2" color="primary" entityType={ENTITY_TYPE.EVIDENCE} entityAction={ENTITY_ACTION.ADD} />
      </h2>
      <div>
        {props.evidenceList && (
          <OncoKBAsyncTable
            data={props.evidenceList.concat()}
            columns={columns}
            loading={props.loading}
            initialPaginationState={getPaginationFromSearchParams(props.location.search) ?? defaultPaginationState}
            searchEntities={props.searchEntities}
            getEntities={props.getEntities}
            totalItems={props.totalItems}
          />
        )}
      </div>
    </div>
  );
};

const mapStoreToProps = ({ evidenceStore }: IRootStore) => ({
  evidenceList: evidenceStore.entities,
  loading: evidenceStore.loading,
  totalItems: evidenceStore.totalItems,
  getEntities: evidenceStore.getEntities,
  searchEntities: evidenceStore.searchEntities,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(Evidence);
