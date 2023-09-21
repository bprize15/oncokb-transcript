import React, { useState, useEffect } from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText, Label, Input, FormGroup } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootStore } from 'app/stores';

import { IFlag } from 'app/shared/model/flag.model';
import { IEnsemblGene } from 'app/shared/model/ensembl-gene.model';
import { ITranscript } from 'app/shared/model/transcript.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { SaveButton } from 'app/shared/button/SaveButton';

export interface ITranscriptUpdateProps extends StoreProps, RouteComponentProps<{ id: string }> {}

export const TranscriptUpdate = (props: ITranscriptUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const flags = props.flags.filter(flag => flag.type === 'TRANSCRIPT');
  const ensemblGenes = props.ensemblGenes;
  const transcriptEntity = props.transcriptEntity;
  const loading = props.loading;
  const updating = props.updating;
  const updateSuccess = props.updateSuccess;

  const handleClose = () => {
    props.history.push('/transcript' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getFlags({});
    props.getEnsemblGenes({});
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...transcriptEntity,
      ...values,
      flags: mapIdList(values.flags),
    };

    if (isNew) {
      props.createEntity(entity);
    } else {
      props.updateEntity(entity);
    }
  };

  const ensemblGeneId = transcriptEntity?.ensemblGene?.id;
  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...transcriptEntity,
          flags: transcriptEntity?.flags?.map(e => e.id.toString()),
          ensemblGene: ensemblGeneId
            ? {
                id: ensemblGeneId,
              }
            : null,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="oncokbCurationApp.transcript.home.createOrEditLabel" data-cy="TranscriptCreateUpdateHeading">
            Create or edit a Transcript
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="transcript-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Ensembl Transcript Id"
                id="transcript-ensemblTranscriptId"
                name="ensemblTranscriptId"
                data-cy="ensemblTranscriptId"
                type="text"
              />
              <ValidatedField label="Canonical" id="transcript-canonical" name="canonical" data-cy="canonical" check type="checkbox" />
              <ValidatedField
                label="Ensembl Protein Id"
                id="transcript-ensemblProteinId"
                name="ensemblProteinId"
                data-cy="ensemblProteinId"
                type="text"
              />
              <ValidatedField
                label="Reference Sequence Id"
                id="transcript-referenceSequenceId"
                name="referenceSequenceId"
                data-cy="referenceSequenceId"
                type="text"
              />
              <ValidatedField label="Description" id="transcript-description" name="description" data-cy="description" type="text" />
              <ValidatedField label="Flag" id="transcript-flag" data-cy="flag" type="select" multiple name="flags">
                <option value="" key="0" />
                {flags
                  ? flags.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.flag}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              {!isNew && transcriptEntity?.ensemblGene ? (
                <FormGroup>
                  <Label for={'ensembl-gene'}>EnsemblGene</Label>
                  <Input
                    id={'ensembl-gene'}
                    name={'ensembl-gene'}
                    autoComplete="off"
                    disabled
                    value={transcriptEntity.ensemblGene.ensemblGeneId}
                  />
                </FormGroup>
              ) : null}
              <SaveButton disabled={updating} />
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStoreToProps = (storeState: IRootStore) => ({
  flags: storeState.flagStore.entities,
  ensemblGenes: storeState.ensemblGeneStore.entities,
  transcriptEntity: storeState.transcriptStore.entity,
  loading: storeState.transcriptStore.loading,
  updating: storeState.transcriptStore.updating,
  updateSuccess: storeState.transcriptStore.updateSuccess,
  getFlags: storeState.flagStore.getEntities,
  getEnsemblGenes: storeState.ensemblGeneStore.getEntities,
  getEntity: storeState.transcriptStore.getEntity,
  updateEntity: storeState.transcriptStore.updateEntity,
  createEntity: storeState.transcriptStore.createEntity,
  reset: storeState.transcriptStore.reset,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(TranscriptUpdate);
