import { RasterMapListDto } from '../RasterMapModel/RasterMapListDto';
export interface FinalMapDto {
  id: number;
  title: string;
  description: string;
  tags: string[];
  fileGeoJson: string;
  rasterMaps: RasterMapListDto[];
}
