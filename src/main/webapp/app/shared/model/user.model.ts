export interface IUser {
  id?: any;
  login: string;
  // the following three elements will be made required.
  firstName: string;
  lastName: string;
  email: string;
  activated?: boolean;
  langKey?: string;
  authorities?: any[];
  createdBy?: string;
  createdDate?: Date | null;
  lastModifiedBy?: string;
  lastModifiedDate?: Date | null;
  imageUrl?: string;
}

export const defaultValue: Readonly<IUser> = {
  id: '',
  login: '',
  firstName: '',
  lastName: '',
  email: '',
  activated: true,
  langKey: '',
  authorities: [],
  createdBy: '',
  createdDate: null,
  lastModifiedBy: '',
  lastModifiedDate: null,
  imageUrl: '',
};
