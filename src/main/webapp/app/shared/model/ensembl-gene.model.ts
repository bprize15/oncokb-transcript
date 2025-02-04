import { ITranscript } from 'app/shared/model/transcript.model';
import { IGene } from 'app/shared/model/gene.model';
import { ISeqRegion } from 'app/shared/model/seq-region.model';
import { ReferenceGenome } from 'app/shared/model/enumerations/reference-genome.model';

export interface IEnsemblGene {
  id: number;
  referenceGenome: ReferenceGenome | null;
  ensemblGeneId: string;
  canonical: boolean;
  start: number;
  end: number;
  strand: number;
  transcripts: ITranscript[] | null;
  gene: IGene | null;
  seqRegion: ISeqRegion | null;
}
