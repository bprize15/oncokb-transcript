import { IFdaSubmission } from 'app/shared/model/fda-submission.model';
import { FdaSubmissionTypeKey } from 'app/shared/model/enumerations/fda-submission-type-key.model';

// CrudStore cannot use interface
export type IFdaSubmissionType = {
  id: number;
  type: FdaSubmissionTypeKey;
  name: string;
  shortName: string | null;
  submissionPrefix: string | null;
  submissionLink: string | null;
  description: string | null;
  fdaSubmissions: IFdaSubmission[] | null;
};
