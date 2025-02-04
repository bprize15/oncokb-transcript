import React, { useEffect } from 'react';
import { connect } from 'app/shared/util/typed-inject';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootStore } from 'app/stores';
export interface IClinicalTrialDeleteDialogProps extends StoreProps, RouteComponentProps<{ id: string }> {}

export const ClinicalTrialDeleteDialog = (props: IClinicalTrialDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const clinicalTrialEntity = props.clinicalTrialEntity;
  const updateSuccess = props.updateSuccess;

  const handleClose = () => {
    props.history.push('/clinical-trial' + props.location.search);
  };

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(clinicalTrialEntity.id);
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="clinicalTrialDeleteDialogHeading">
        Confirm delete operation
      </ModalHeader>
      <ModalBody id="oncokbCurationApp.clinicalTrial.delete.question">Are you sure you want to delete this ClinicalTrial?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        <Button id="jhi-confirm-delete-clinicalTrial" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Delete
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStoreToProps = ({ clinicalTrialStore }: IRootStore) => ({
  clinicalTrialEntity: clinicalTrialStore.entity,
  updateSuccess: clinicalTrialStore.updateSuccess,
  getEntity: clinicalTrialStore.getEntity,
  deleteEntity: clinicalTrialStore.deleteEntity,
});

type StoreProps = ReturnType<typeof mapStoreToProps>;

export default connect(mapStoreToProps)(ClinicalTrialDeleteDialog);
