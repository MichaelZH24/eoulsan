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

package fr.ens.transcriptome.eoulsan.bio.readsfilters;

import static fr.ens.transcriptome.eoulsan.util.Utils.newArrayList;

import java.util.List;

import fr.ens.transcriptome.eoulsan.bio.ReadSequence;
import fr.ens.transcriptome.eoulsan.util.ReporterIncrementer;

/**
 * This class define a read filter that calls successively a list of read
 * filters.
 * @author Laurent Jourdren
 */
public class MultiReadFilter implements ReadFilter {

  private final List<ReadFilter> list = newArrayList();
  private final ReporterIncrementer incrementer;
  private final String counterGroup;

  @Override
  public boolean accept(final ReadSequence read) {

    if (read == null)
      return false;

    for (ReadFilter rf : this.list) {

      if (!rf.accept(read)) {

        if (incrementer != null) {
          this.incrementer.incrCounter(counterGroup,
              "reads rejected by " + rf.getName() + " filter", 1);
        }
        return false;
      }

    }

    return true;
  }

  @Override
  public boolean accept(final ReadSequence read1, final ReadSequence read2) {

    for (ReadFilter rf : this.list) {

      if (!rf.accept(read1, read2)) {

        if (incrementer != null) {
          this.incrementer.incrCounter(counterGroup,
              "reads rejected by " + rf.getName() + " filter", 1);
        }
        return false;
      }

    }

    return true;
  }

  /**
   * Add a filter to the multi filter.
   * @param filter filter to add
   */
  public void addFilter(final ReadFilter filter) {

    if (filter != null) {
      this.list.add(filter);
    }

  }

  @Override
  public String getName() {

    return "MultiReadFilter";
  }

  @Override
  public String getDescription() {

    return "Multi read filter";
  }

  @Override
  public void setParameter(String key, String value) {
    // This filter has no parameter
  }

  @Override
  public void init() {
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   */
  public MultiReadFilter() {

    this((ReporterIncrementer) null, null);
  }

  /**
   * Public constructor.
   * @param incrementer incrementer to use
   * @param counterGroup counter group for the incrementer
   */
  public MultiReadFilter(final ReporterIncrementer incrementer,
      final String counterGroup) {

    this.incrementer = incrementer;
    this.counterGroup = counterGroup;
  }

  /**
   * Public constructor.
   * @param filters filters to add
   */
  public MultiReadFilter(final List<ReadFilter> filters) {

    this(null, null, filters);
  }

  /**
   * Public constructor.
   * @param incrementer incrementer to use
   * @param counterGroup counter group for the incrementer
   * @param filters filters to add
   */
  public MultiReadFilter(final ReporterIncrementer incrementer,
      final String counterGroup, final List<ReadFilter> filters) {

    this.incrementer = incrementer;
    this.counterGroup = counterGroup;

    if (filters != null) {

      for (ReadFilter filter : filters) {
        addFilter(filter);
      }
    }
  }

}
