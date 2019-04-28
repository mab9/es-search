export interface Technology {
  name?: string;
  yearsOfExperience?: number;
}

export interface Contact {
  id: number | string;
  firstName?: string;
  lastName?: string;
  technologies?: Technology[];
  email?: string;
  phone?: string | string[];
}
