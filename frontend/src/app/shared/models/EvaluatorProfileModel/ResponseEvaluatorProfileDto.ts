import { SaveDiseaseExperienceDto } from './SaveDiseaseExperienceDto';

export interface ResponseEvaluatorProfileDto {
  id: number;
  professions: string[];
  sectors: string[];
  interventionLevels: string[];
  sectorsWorkedIn: string[];
  countries: string;
  regions: string;
  divisions: string;
  rvfExperience: SaveDiseaseExperienceDto;
  evdExperience: SaveDiseaseExperienceDto;
}
