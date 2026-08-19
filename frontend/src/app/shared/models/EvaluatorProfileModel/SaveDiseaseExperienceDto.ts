export interface SaveDiseaseExperienceDto {
  pathogenKnowledgeScore: number;
  transmissionKnowledgeScore: number;
  animalClinicalKnowledgeScore: number;
  humanClinicalKnowledgeScore: number;
  professionallyExposed: boolean;
  exposureFrequency: string;
  yearsInvolved: string;
}
