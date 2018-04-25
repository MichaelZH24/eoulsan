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
 * of the Institut de Biologie de l'École normale supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://outils.genomique.biologie.ens.fr/eoulsan
 *
 */

package fr.ens.biologie.genomique.eoulsan.modules.expression;

import static fr.ens.biologie.genomique.eoulsan.EoulsanLogger.getLogger;
import static fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter.ATTRIBUTE_ID_PARAMETER_NAME;
import static fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter.GENOMIC_TYPE_PARAMETER_NAME;
import static fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter.OVERLAP_MODE_PARAMETER_NAME;
import static fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter.REMOVE_AMBIGUOUS_CASES_PARAMETER_NAME;
import static fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter.SPLIT_ATTRIBUTE_VALUES_PARAMETER_NAME;
import static fr.ens.biologie.genomique.eoulsan.core.Modules.renamedParameter;
import static fr.ens.biologie.genomique.eoulsan.core.OutputPortsBuilder.singleOutputPort;
import static fr.ens.biologie.genomique.eoulsan.data.DataFormats.ANNOTATION_GFF;
import static fr.ens.biologie.genomique.eoulsan.data.DataFormats.ANNOTATION_GTF;
import static fr.ens.biologie.genomique.eoulsan.data.DataFormats.EXPRESSION_RESULTS_TSV;
import static fr.ens.biologie.genomique.eoulsan.data.DataFormats.GENOME_DESC_TXT;
import static fr.ens.biologie.genomique.eoulsan.data.DataFormats.MAPPER_RESULTS_SAM;

import java.util.Set;

import fr.ens.biologie.genomique.eoulsan.AbstractEoulsanRuntime.EoulsanExecMode;
import fr.ens.biologie.genomique.eoulsan.EoulsanException;
import fr.ens.biologie.genomique.eoulsan.Globals;
import fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.ExpressionCounter;
import fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.ExpressionCounterService;
import fr.ens.biologie.genomique.eoulsan.bio.expressioncounters.HTSeqCounter;
import fr.ens.biologie.genomique.eoulsan.core.InputPorts;
import fr.ens.biologie.genomique.eoulsan.core.InputPortsBuilder;
import fr.ens.biologie.genomique.eoulsan.core.Modules;
import fr.ens.biologie.genomique.eoulsan.core.OutputPorts;
import fr.ens.biologie.genomique.eoulsan.core.Parameter;
import fr.ens.biologie.genomique.eoulsan.core.StepConfigurationContext;
import fr.ens.biologie.genomique.eoulsan.core.Version;
import fr.ens.biologie.genomique.eoulsan.modules.AbstractModule;
import fr.ens.biologie.genomique.eoulsan.modules.CheckerModule;

/**
 * This abstract class define and parse arguments for the expression module.
 * @since 1.0
 * @author Laurent Jourdren
 * @author Maria Bernard
 * @author Claire Wallon
 */
public abstract class AbstractExpressionModule extends AbstractModule {

  public static final String MODULE_NAME = "expression";
  protected static final String COUNTER_GROUP = "expression";

  public static final String COUNTER_PARAMETER_NAME = "counter";
  public static final String FEATURES_FILE_FORMAT_PARAMETER_NAME =
      "features.file.format";

  private static final String OLD_EOULSAN_COUNTER_NAME = "eoulsanCounter";
  private static final String OLD_REMOVE_AMBIGUOUS_CASES_PARAMETER_NAME =
      "removeambiguouscases";
  private static final String OLD_OVERLAP_MODE_PARAMETER_NAME = "overlapmode";
  private static final String OLD_GENOMIC_TYPE_PARAMETER_NAME = "genomictype";
  private static final String OLD_ATTRIBUTE_ID_PARAMETER_NAME = "attributeid";
  private static final String OLD_SPLIT_ATTRIBUTE_VALUES_PARAMETER_NAME =
      "splitattributevalues";

  private boolean gtfFormat;
  private ExpressionCounter counter;

  //
  // Protected methods
  //

  /**
   * Test if GTF format must be used.
   * @return true if GTF format must be used
   */
  protected boolean isGTFFormat() {
    return this.gtfFormat;
  }

  /**
   * Get the counter.
   * @return the counter
   */
  protected ExpressionCounter getExpressionCounter() {

    return this.counter;
  }

  //
  // Module methods
  //

  @Override
  public String getName() {

    return MODULE_NAME;
  }

  @Override
  public String getDescription() {

    return "This module compute the expression.";
  }

  @Override
  public Version getVersion() {

    return Globals.APP_VERSION;
  }

  @Override
  public InputPorts getInputPorts() {

    final InputPortsBuilder builder = new InputPortsBuilder();

    builder.addPort("alignments", MAPPER_RESULTS_SAM);
    builder.addPort("featuresannotation",
        this.gtfFormat ? ANNOTATION_GTF : ANNOTATION_GFF);
    builder.addPort("genomedescription", GENOME_DESC_TXT);

    return builder.create();
  }

  @Override
  public OutputPorts getOutputPorts() {
    return singleOutputPort(EXPRESSION_RESULTS_TSV);
  }

  @Override
  public void configure(final StepConfigurationContext context,
      final Set<Parameter> stepParameters) throws EoulsanException {

    Parameter counterParameter =
        Modules.getParameter(stepParameters, COUNTER_PARAMETER_NAME);

    // The original Eoulsan counter is no more supported
    if (counterParameter == null) {
      Modules.invalidConfiguration(context, "No counter to use defined");
    }

    // Get the counter name to use
    String counterName = counterParameter.getLowerStringValue();

    // Check if user wants to use the very old counter
    if (OLD_EOULSAN_COUNTER_NAME.toLowerCase().equals(counterName)) {
      getLogger().warning("The "
          + OLD_EOULSAN_COUNTER_NAME + " counter support has been removed");
    }

    // Create an instance of the counter
    this.counter =
        ExpressionCounterService.getInstance().newService(counterName);

    // Test if counter engine exists
    if (this.counter == null) {
      Modules.invalidConfiguration(context, "Unknown counter: " + counterName);
    }

    // Handle old parameter names for HTSeq-count counter
    if (HTSeqCounter.COUNTER_NAME.equals(counterName)) {

      for (Parameter p : stepParameters) {

        switch (p.getName()) {

        case OLD_GENOMIC_TYPE_PARAMETER_NAME:
          renamedParameter(context, p, GENOMIC_TYPE_PARAMETER_NAME, true);
          break;

        case OLD_ATTRIBUTE_ID_PARAMETER_NAME:
          renamedParameter(context, p, ATTRIBUTE_ID_PARAMETER_NAME, true);
          break;

        case OLD_OVERLAP_MODE_PARAMETER_NAME:
          renamedParameter(context, p, OVERLAP_MODE_PARAMETER_NAME, true);
          break;

        case OLD_REMOVE_AMBIGUOUS_CASES_PARAMETER_NAME:
          renamedParameter(context, p, REMOVE_AMBIGUOUS_CASES_PARAMETER_NAME,
              true);
          break;

        case OLD_SPLIT_ATTRIBUTE_VALUES_PARAMETER_NAME:
          renamedParameter(context, p, SPLIT_ATTRIBUTE_VALUES_PARAMETER_NAME,
              true);
          break;
        }
      }
    }

    for (Parameter p : stepParameters) {

      switch (p.getName()) {

      case COUNTER_PARAMETER_NAME:
        // Do nothing as the counter to use has been already set
        break;

      case FEATURES_FILE_FORMAT_PARAMETER_NAME:

        switch (p.getLowerStringValue()) {

        case "gtf":
          this.gtfFormat = true;
          break;

        case "gff":
        case "gff3":
          this.gtfFormat = false;
          break;

        default:
          Modules.badParameterValue(context, p,
              "Unknown annotation file format");
          break;
        }

        break;

      default:
        try {
          this.counter.setParameter(p.getName(), p.getValue());
        } catch (EoulsanException e) {
          throw new EoulsanException("The invalid value ("
              + p.getValue() + ") for \"" + p.getName()
              + "\" parameter in the \"" + context.getCurrentStep().getId()
              + "\" step: " + e.getMessage(), e);
        }
      }

    }

    // Check the counter configuration
    this.counter.checkConfiguration();

    // Configure Checker
    if (context.getRuntime().getMode() != EoulsanExecMode.CLUSTER_TASK) {
      CheckerModule.configureChecker(
          this.gtfFormat ? ANNOTATION_GTF : ANNOTATION_GFF, stepParameters);
    }

    // Log Step parameters
    getLogger().info("In " + getName() + ", counter=" + this.counter.getName());
  }

}
