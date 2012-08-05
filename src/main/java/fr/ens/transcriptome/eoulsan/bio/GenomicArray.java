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

package fr.ens.transcriptome.eoulsan.bio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.eoulsan.util.Utils;

/**
 * This class define a genomic array. TODO more doc and rename attributes and
 * field of the inner classes
 * @since 1.2
 * @author Laurent Jourdren
 */
public class GenomicArray<T> {

  private final Map<String, ChromosomeZones<T>> chromosomes = Utils
      .newHashMap();

  /**
   * This class define a zone in a ChromosomeZone object.
   * @author Laurent Jourdren
   */
  private static final class Zone<T> implements Serializable {

    private static final long serialVersionUID = 3581472137861260840L;

    private final int start;
    private int end;
    private final char strand;

    private Set<T> _values;
    private T _value;
    private int valueCount;

    /**
     * Add a value to the zone.
     * @param value Exon to add
     */
    public void addExon(final T value) {

      if (value == null)
        return;

      if (valueCount == 0) {
        this._value = value;
        this.valueCount = 1;
      } else {

        if (valueCount == 1) {

          if (value == this._value
              || this._value.hashCode() == value.hashCode())
            return;

          this._values = new HashSet<T>();
          this._values.add(this._value);
          this._value = null;
        }

        this._values.add(value);
        this.valueCount = this._values.size();
      }
    }

    /**
     * Add values to the zone.
     * @param values values to add
     */
    private void addExons(final Set<T> values) {

      if (values == null)
        return;

      final int len = values.size();

      if (len == 0)
        return;

      if (len == 1) {
        this._value = values.iterator().next();
        this.valueCount = this._value == null ? 0 : 1;
      } else {
        this._values = new HashSet<T>(values);
        this.valueCount = len;
      }

    }

    /**
     * Get the values of the zone.
     * @return a set with the values of the zone
     */
    public Set<T> getValues() {

      if (this.valueCount == 0)
        return null;

      if (this.valueCount == 1)
        return Collections.singleton(this._value);

      return this._values;
    }

    /**
     * Test if a position is before, in or after the zone.
     * @param position to test
     * @return -1 if position is before the zone, 0 if the position is in the
     *         zone and 1 of the position is after the zone
     */
    public int compareTo(final int position) {

      if (position >= this.start && position <= this.end)
        return 0;

      return position < this.start ? -1 : 1;
    }

    @Override
    public String toString() {

      Set<String> r = new HashSet<String>();
      if (getValues() != null)
        for (T e : getValues())
          r.add(e.toString());

      return "[" + this.start + "," + this.end + "," + r + "]";
    }

    //
    // Constructor
    //

    /**
     * Constructor that create a zone
     * @param start start position of the zone
     * @param end end position of the zone
     * @param strand strand of the zone
     */
    public Zone(final int start, final int end, final char strand) {

      this.start = start;
      this.end = end;
      this.strand = strand;
    }

    /**
     * Constructor that create a zone
     * @param start start position of the zone
     * @param end end postion of the zone
     * @param strand strand of the zone
     * @param exons of the zone
     */
    public Zone(final int start, final int end, final char strand,
        final Set<T> exons) {

      this(start, end, strand);
      addExons(exons);
    }

  }

  /**
   * This class define an object that contains all the stranded zones of a
   * chromosome.
   * @author Laurent Jourdren
   */
  private static final class ChromosomeStrandedZones<T> implements Serializable {

    private static final long serialVersionUID = 8073207058699194059L;

    private final String chromosomeName;
    private int length = 0;
    private final List<Zone<T>> zones = new ArrayList<Zone<T>>();

    private final Zone<T> get(final int index) {

      return this.zones.get(index);
    }

    /**
     * Add a zone.
     * @param zone zone to add
     */
    private final void add(final Zone<T> zone) {

      this.zones.add(zone);
    }

    /**
     * Add a zone.
     * @param index index where add the zone
     * @param zone the zone to add
     */
    private final void add(final int index, final Zone<T> zone) {

      this.zones.add(index, zone);
    }

    /**
     * Find the zone index for a position
     * @param pos the position on the chromosome
     * @return the index of the zone or -1 if the position if lower than 1 or
     *         greater than the length of the chromosome
     */
    private int findIndexPos(final int pos) {

      if (pos < 1 || pos > this.length)
        return -1;

      int minIndex = 0;
      int maxIndex = zones.size() - 1;
      int index = 0;

      while (true) {

        final int diff = maxIndex - minIndex;
        index = minIndex + diff / 2;

        if (diff == 1) {

          if (get(minIndex).compareTo(pos) == 0)
            return minIndex;
          if (get(maxIndex).compareTo(pos) == 0)
            return maxIndex;

          assert (false);
        }

        final Zone<T> z = get(index);

        final int comp = z.compareTo(pos);
        if (comp == 0)
          return index;

        if (comp < 0)
          maxIndex = index;
        else
          minIndex = index;
      }
    }

    // private int findIndexPos(final int pos, final char strand,
    // final String stranded) {
    //
    // if (pos < 1 || pos > this.length)
    // return -1;
    //
    // int minIndex = 0;
    // int maxIndex = zones.size() - 1;
    // int index = 0;
    //
    // while (true) {
    //
    // final int diff = maxIndex - minIndex;
    // index = minIndex + diff / 2;
    //
    // if (diff == 1) {
    //
    // if (get(minIndex).compareTo(pos) == 0 && get(minIndex).strand == strand)
    // return minIndex;
    // if (get(maxIndex).compareTo(pos) == 0 && get(minIndex).strand == strand)
    // return maxIndex;
    //
    // assert (false);
    // }
    //
    // final Zone<T> z = get(index);
    //
    // final int comp = z.compareTo(pos);
    // if (comp == 0 && z.strand == strand)
    // return index;
    //
    // if (comp < 0)
    // maxIndex = index;
    // else
    // minIndex = index;
    // }
    // }

    /**
     * Split a zone in two zone.
     * @param zone zone to split
     * @param pos position of the split
     * @return a new zone object
     */
    private Zone<T> splitZone(final Zone<T> zone, final int pos) {

      final Zone<T> result =
          new Zone<T>(pos, zone.end, zone.strand, zone.getValues());
      zone.end = pos - 1;

      return result;
    }

    /**
     * Add an entry.
     * @param interval interval of the entry
     * @param value value to add
     */
    public void addEntry(final GenomicInterval interval, final T value) {

      final int intervalStart = interval.getStart();
      final int intervalEnd = interval.getEnd();

      // Create an empty zone if the interval is after the end of the
      // last chromosome zone
      if (interval.getEnd() > this.length) {
        add(new Zone<T>(this.length + 1, intervalEnd, interval.getStrand()));
        this.length = intervalEnd;
      }

      final int indexStart = findIndexPos(intervalStart);
      final int indexEnd = findIndexPos(intervalEnd);

      final Zone<T> z1 = get(indexStart);
      final Zone<T> z1b;
      final int count1b;

      if (z1.start == intervalStart) {
        z1b = z1;
        count1b = 0;
      } else {
        z1b = splitZone(z1, intervalStart);
        count1b = 1;
      }

      // Same index
      if (indexStart == indexEnd) {

        if (z1b.end == intervalEnd) {
          z1b.addExon(value);
        } else {

          final Zone<T> z1c = splitZone(z1b, intervalEnd + 1);
          add(indexStart + 1, z1c);
        }

        if (z1 != z1b) {
          z1b.addExon(value);
          add(indexStart + 1, z1b);

        } else
          z1.addExon(value);

      } else {

        final Zone<T> z2 = get(indexEnd);
        final Zone<T> z2b;

        if (z2.end != intervalEnd) {
          z2b = splitZone(z2, intervalEnd + 1);
        } else
          z2b = z2;

        if (z1 != z1b) {
          add(indexStart + 1, z1b);
        }

        if (z2 != z2b)
          add(indexEnd + 1 + count1b, z2b);

        for (int i = indexStart + count1b; i <= indexEnd + count1b; i++) {
          get(i).addExon(value);
        }
      }
    }

    // public void addEntry(final GenomicInterval interval, final T value,
    // final String stranded) {
    //
    // final int intervalStart = interval.getStart();
    // final int intervalEnd = interval.getEnd();
    // final char intervalStrand = interval.getStrand();
    //
    // // Create an empty zone if the interval is after the end of the
    // // last chromosome zone
    // if (interval.getEnd() > this.length) {
    // add(new Zone<T>(this.length + 1, intervalEnd, interval.getStrand()));
    // this.length = intervalEnd;
    // }
    //
    // final int indexStart;
    // final int indexEnd;
    //
    // if (stranded.equals("no")) {
    // indexStart = findIndexPos(intervalStart);
    // indexEnd = findIndexPos(intervalEnd);
    // } else {
    // indexStart =
    // findIndexPos(intervalStart, intervalStrand, stranded);
    // indexEnd = findIndexPos(intervalEnd, intervalStrand, stranded);
    // }
    //
    // final Zone<T> z1 = get(indexStart);
    // final Zone<T> z1b;
    // final int count1b;
    //
    // if (z1.start == intervalStart) {
    // z1b = z1;
    // count1b = 0;
    // } else {
    // z1b = splitZone(z1, intervalStart);
    // count1b = 1;
    // }
    //
    // // Same index
    // if (indexStart == indexEnd) {
    //
    // if (z1b.end == intervalEnd) {
    // z1b.addExon(value);
    // } else {
    //
    // final Zone<T> z1c = splitZone(z1b, intervalEnd + 1);
    // add(indexStart + 1, z1c);
    // }
    //
    // if (z1 != z1b) {
    // z1b.addExon(value);
    // add(indexStart + 1, z1b);
    //
    // } else
    // z1.addExon(value);
    //
    // } else {
    //
    // final Zone<T> z2 = get(indexEnd);
    // final Zone<T> z2b;
    //
    // if (z2.end != intervalEnd) {
    // z2b = splitZone(z2, intervalEnd + 1);
    // } else
    // z2b = z2;
    //
    // if (z1 != z1b) {
    // add(indexStart + 1, z1b);
    // }
    //
    // if (z2 != z2b)
    // add(indexEnd + 1 + count1b, z2b);
    //
    // for (int i = indexStart + count1b; i <= indexEnd + count1b; i++) {
    // get(i).addExon(value);
    // }
    // }
    // }

    /**
     * Get entries.
     * @param start start of the interval
     * @param stop end of the interval
     * @return a map with the values
     */
    public Map<GenomicInterval, T> getEntries(final int start, final int stop) {

      final int indexStart = findIndexPos(start);
      final int indexEnd = findIndexPos(stop);

      if (indexStart == -1)
        return null;

      final int from = indexStart;
      final int to = indexEnd == -1 ? this.zones.size() - 1 : indexEnd;

      Map<GenomicInterval, T> result = null;

      for (int i = from; i <= to; i++) {

        final Zone<T> zone = get(i);
        final Set<T> r = zone.getValues();
        if (r != null) {

          for (T e : r)

            // Really needed ?
            if (intersect(start, stop, zone.start, zone.end)) {

              if (result == null)
                result = Utils.newHashMap();

              // if (chromosomeName.equals("chr2")) {
              // System.out.println(e);
              // System.out.println("zone.start : " + zone.start);
              // System.out.println("zone.end : " + zone.end);
              // System.out.println("zone.strand : " + zone.strand);
              // }

              result.put(new GenomicInterval(this.chromosomeName, zone.start,
                  zone.end, zone.strand), e);
            }

        }
      }

      return result;
    }

    /**
     * Test if an interval intersect a zone.
     * @param start start of the interval
     * @param end end of the interval
     * @param startZone start of the zone
     * @param endZone end of the zone
     * @return true if the interval intersect a zone
     */
    private static final boolean intersect(final int start, final int end,
        final int startZone, final int endZone) {

      return (start >= startZone && start <= endZone)
          || (end >= startZone && end <= endZone)
          || (start < startZone && end > endZone);
    }

    //
    // Constructor
    //

    /**
     * Public constructor.
     * @param chromosomeName name of the chromosome
     */
    public ChromosomeStrandedZones(final String chromosomeName) {

      this.chromosomeName = chromosomeName;
    }
  }

  /**
   * This class define an object that contains all the zones of a chromosome.
   * These zones are stranded if "yes" or "reverse".
   * @author Claire Wallon
   */
  private static final class ChromosomeZones<T> implements Serializable {

    private static final long serialVersionUID = -6312870823086177216L;

    private ChromosomeStrandedZones<T> plus;
    private ChromosomeStrandedZones<T> minus;

    /**
     * Add a stranded entry.
     * @param interval interval of the entry
     * @param value value to add
     */
    public void addEntry(final GenomicInterval interval, final T value) {

      if (interval.getStrand() == '+' || interval.getStrand() == '.')
        plus.addEntry(interval, value);
      else if (interval.getStrand() == '-')
        minus.addEntry(interval, value);
    }

    /**
     * Get stranded entries.
     * @param start start of the interval
     * @param stop end of the interval
     * @return a map with the values
     */
    public Map<GenomicInterval, T> getEntries(final int start, final int stop) {

      Map<GenomicInterval, T> result = new HashMap<GenomicInterval, T>();
      Map<GenomicInterval, T> inter = new HashMap<GenomicInterval, T>();

      inter = plus.getEntries(start, stop);
      if (inter != null)
        result.putAll(inter);
      inter = minus.getEntries(start, stop);
      if (inter != null)
        result.putAll(inter);

      return result;

    }

    //
    // Constructor
    //

    /**
     * Public constructor.
     * @param chromosomeName name of the chromosome
     */
    public ChromosomeZones(final String chromosomeName) {

      this.plus = new ChromosomeStrandedZones<T>(chromosomeName);
      this.minus = new ChromosomeStrandedZones<T>(chromosomeName);
    }
  }

  /**
   * Add an entry on the genomic array.
   * @param interval genomic interval
   * @param value value to add
   */
  public void addEntry(final GenomicInterval interval, final T value) {

    if (interval == null)
      return;

    final String chromosomeName = interval.getChromosome();
    final ChromosomeZones<T> chr;

    // Create a ChromosomeZones if it does not exist yet
    if (!this.chromosomes.containsKey(chromosomeName)) {
      chr = new ChromosomeZones<T>(chromosomeName);
      this.chromosomes.put(chromosomeName, chr);
    } else
      chr = this.chromosomes.get(chromosomeName);

    // Add the GenomicInterval to the ChromosomeZones
    chr.addEntry(interval, value);
  }

  /**
   * Get entries in an interval.
   * @param interval the genomic interval
   * @return a map with the values
   */
  public Map<GenomicInterval, T> getEntries(final GenomicInterval interval) {

    if (interval == null)
      throw new NullPointerException("The interval is null");

    return getEntries(interval.getChromosome(), interval.getStart(),
        interval.getEnd());
  }

  /**
   * Get entries in an interval
   * @param chromosome chromosome of the interval
   * @param start start of the interval
   * @param end end of the interval
   * @return a map with the values
   */
  public Map<GenomicInterval, T> getEntries(final String chromosome,
      final int start, final int end) {

    final ChromosomeZones<T> chr = this.chromosomes.get(chromosome);

    if (chr == null)
      return null;

    return chr.getEntries(start, end);
  }

  /**
   * Test if the GenomicArray contains a chromosome.
   * @param chromosomeName name of the chromosome to test
   * @return true if the GenomicArray contains the chromosome
   */
  public boolean containsChromosome(final String chromosomeName) {

    if (chromosomeName == null)
      return false;

    return this.chromosomes.containsKey(chromosomeName);
  }

}
