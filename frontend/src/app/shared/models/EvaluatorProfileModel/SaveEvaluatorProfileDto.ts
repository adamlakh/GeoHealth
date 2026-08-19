import { SaveDiseaseExperienceDto } from './SaveDiseaseExperienceDto';

export interface SaveEvaluatorProfileDto {
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
