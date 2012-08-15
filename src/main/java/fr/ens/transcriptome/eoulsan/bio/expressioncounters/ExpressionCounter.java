/*
 *                  Eoulsan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License version 2.1 or
 * later and CeCILL-C. This should be distributed with the code.
 * If you do not have a copy, see:
 *
 *      http://www.gnu.org/licenses/lgpl-2.1.txt
 *      http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.txt
 *
 * Copyright for this code is held jointly by the Genomic platform
 * of the Institut de Biologie de l'École Normale Supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/eoulsan
 *
 */

package fr.ens.transcriptome.eoulsan.bio.expressioncounters;

import java.io.File;
import java.io.IOException;

import fr.ens.transcriptome.eoulsan.data.DataFile;
import fr.ens.transcriptome.eoulsan.util.Reporter;

/**
 * This class define an interface for a wrapper on an expression counter.
 * @since 1.2
 * @author Claire Wallon
 */
public interface ExpressionCounter {

  //
  // Getters
  //

  /**
   * Get the counter name.
   * @return the counter name
   */
  String getCounterName();

  StrandUsage getStranded();

  OverlapMode getOverlapMode();

  String getTempDirectory();

  //
  // Setters
  //

  void setStranded(String stranded);

  void setStranded(StrandUsage stranded);

  void setOverlapMode(String mode);
  
  void setOverlapMode(OverlapMode mode);

  void setTempDirectory(String tempDirectory);

  //
  // Counting methods
  //

  void count(File alignmentFile, DataFile annotationFile, File expressionFile,
      DataFile genomeDescFile) throws IOException;

  //
  // Other methods
  //

  void init(String annotationKey, Reporter reporter, String counterGroup);

}