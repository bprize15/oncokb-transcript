import React, { useState, useEffect } from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { RouteComponentProps } from 'react-router-dom';
import { Row, Col } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { IRootStore } from 'app/stores';

import { SaveButton } from 'app/shared/button/SaveButton';
import { ISpecimenType } from 'app/shared/model/specimen-type.model';

export interface ISpecimenTypeUpdateProps extends StoreProps, RouteComponentProps<{ id: string }> {}

export const SpecimenTypeUpdate = (props: ISpecimenTypeUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const specimenTypeEntity = props.specimenTypeEntity;
  const loading = props.loading;
  const updating = props.updating;
  const updateSuccess = props.updateSuccess;

  const handleClose = () => {
    props.history.push('/specimen-type');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getCompanionDiagnosticDevices({});
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = (values: Partial<ISpecimenType>) => {
    const entity = {
      ...specimenTypeEntity,
      ...values,
    };

    if (isNew) {
      props.createEntity(entity);
    } else {
      props.updateEntity(entity);
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...specimenTypeEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="oncokbCurationApp.specimenType.home.createOrEditLabel" data-cy="SpecimenTypeCreateUpdateHeading">
            Add or edit a SpecimenType
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="specimen-type-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Type"
                id="specimen-type-type"
                name="type"
                data-cy="type"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Name"
                id="specimen-type-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <SaveButton disabled={updating} />
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStoreToProps = (storeState: IRootStore) => ({
  companionDiagnosticDevices: storeState.companionDiagnosticDeviceStore.entities,
  specimenTypeEntity: storeState.specimenTypeStore.entity,
  loading: storeState.specimenTypeStore.loading,
  updating: storeState.specimenTypeStore.updating,
  updateSuccess: storeState.specimenTypeStore.updateSuccess,
  getCompanionDiagnosticDevices: storeState.companionDiagnosticDeviceStore.getEntities,
  getEntity: storeState.specimenTypeStore.getEntity,
  updateEntity: storeState.specimenTypeStore.updateEntity,
  createEntity: storeState.specimenTypeStore.createEntity,
  reset: storeState.specimenTypeStore.reset,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(SpecimenTypeUpdate);
