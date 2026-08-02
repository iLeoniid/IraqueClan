package gg.leo.IraqueClan.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StartupReport {

    private static final String PREFIX =
            ConsoleColors.DARK_GRAY + "[" + ConsoleColors.CYAN + "IraqueClan"
                    + ConsoleColors.DARK_GRAY + "] " + ConsoleColors.RESET;
    private static final String LINE =
            ConsoleColors.DARK_GRAY + "==============================" + ConsoleColors.RESET;

    private final Logger logger;
    private final long startTime;
    private final List<Step> steps = new ArrayList<>();
    private int errors = 0;
    private int warnings = 0;
    private int skipped = 0;

    public StartupReport(Logger logger) {
        this.logger = logger;
        this.startTime = System.currentTimeMillis();
    }

    public void printBanner(String version) {
        this.logger.info("");
        this.logger.info(LINE);
        this.logger.info(ConsoleColors.CYAN + ConsoleColors.BOLD + "  IraqueClan v" + version + ConsoleColors.RESET);
        this.logger.info(ConsoleColors.CYAN + ConsoleColors.BOLD + "  Sistema de Cl\u00e3s" + ConsoleColors.RESET);
        this.logger.info(LINE);
        this.logger.info("");
    }

    public Step startStep(String name) {
        return new Step(name);
    }

    public void finishStep(Step step) {
        step.finish();
        this.steps.add(step);
        if (step.status == Status.ERROR) this.errors++;
        if (step.status == Status.WARN) this.warnings++;
        if (step.status == Status.SKIP) this.skipped++;

        String icon = getColoredIcon(step.status);
        String timeStr = step.elapsed > 0
                ? " " + ConsoleColors.DARK_GRAY + "(" + step.elapsed + "ms)" + ConsoleColors.RESET : "";
        String detail = step.detail != null
                ? " " + ConsoleColors.DARK_GRAY + "- " + getDetailColor(step.status) + step.detail + ConsoleColors.RESET : "";

        this.logger.info(PREFIX + icon + " " + ConsoleColors.YELLOW + step.name + ConsoleColors.RESET + timeStr + detail);
    }

    public void finishStep(Step step, String detail) {
        step.detail = detail;
        finishStep(step);
    }

    public void finishStepError(Step step, String error) {
        step.status = Status.ERROR;
        step.detail = error;
        finishStep(step);
    }

    public void finishStepWarning(Step step, String warning) {
        step.status = Status.WARN;
        step.detail = warning;
        finishStep(step);
    }

    public void finishStepSkipped(Step step, String reason) {
        step.status = Status.SKIP;
        step.detail = reason;
        finishStep(step);
    }

    public void printSummary(String version) {
        long totalMs = System.currentTimeMillis() - this.startTime;
        int loaded = 0;
        for (Step s : this.steps) {
            if (s.status == Status.OK) loaded++;
        }

        this.logger.info("");
        this.logger.info(LINE);
        this.logger.info(ConsoleColors.YELLOW + ConsoleColors.BOLD + "  Relat\u00f3rio de Inicializa\u00e7\u00e3o" + ConsoleColors.RESET);
        this.logger.info(LINE);
        this.logger.info("");

        for (Step step : this.steps) {
            String icon = getColoredIcon(step.status);
            String nameColor = step.status == Status.ERROR ? ConsoleColors.RED :
                    step.status == Status.WARN ? ConsoleColors.YELLOW :
                            step.status == Status.SKIP ? ConsoleColors.GRAY : ConsoleColors.GREEN;
            this.logger.info(PREFIX + "  " + icon + " " + nameColor + step.name + ConsoleColors.RESET);
        }

        this.logger.info("");
        this.logger.info(PREFIX + ConsoleColors.YELLOW + ConsoleColors.BOLD + " Resumo:" + ConsoleColors.RESET);
        this.logger.info(PREFIX + "  " + ConsoleColors.GRAY + "Steps: " + ConsoleColors.GREEN + loaded
                + ConsoleColors.GRAY + "/" + ConsoleColors.WHITE + this.steps.size()
                + ConsoleColors.GRAY + " carregados" + ConsoleColors.RESET);
        if (this.errors > 0) {
            this.logger.info(PREFIX + "  " + ConsoleColors.GRAY + "Erros: " + ConsoleColors.RED + this.errors + ConsoleColors.RESET);
        }
        if (this.warnings > 0) {
            this.logger.info(PREFIX + "  " + ConsoleColors.GRAY + "Avisos: " + ConsoleColors.YELLOW + this.warnings + ConsoleColors.RESET);
        }
        if (this.skipped > 0) {
            this.logger.info(PREFIX + "  " + ConsoleColors.GRAY + "Pulados: " + ConsoleColors.GRAY + this.skipped + ConsoleColors.RESET);
        }
        this.logger.info(PREFIX + "  " + ConsoleColors.GRAY + "Tempo: " + ConsoleColors.WHITE + totalMs + "ms" + ConsoleColors.RESET);

        this.logger.info("");
        if (this.errors == 0) {
            this.logger.info(PREFIX + ConsoleColors.GREEN + ConsoleColors.BOLD + "  \u2713 Plugin habilitado com sucesso! "
                    + ConsoleColors.GREEN + "Nenhum erro detectado." + ConsoleColors.RESET);
        } else {
            this.logger.info(PREFIX + ConsoleColors.RED + ConsoleColors.BOLD + "  \u2717 Plugin habilitado com "
                    + ConsoleColors.RED + this.errors + " erro(s). " + ConsoleColors.RED + "Verifique os logs acima." + ConsoleColors.RESET);
        }
        this.logger.info(LINE);
        this.logger.info("");
    }

    public boolean hasErrors() {
        return this.errors > 0;
    }

    public int getErrorCount() {
        return this.errors;
    }

    public int getWarningCount() {
        return this.warnings;
    }

    private String getColoredIcon(Status status) {
        return switch (status) {
            case OK -> ConsoleColors.GREEN + ConsoleColors.BOLD + "[\u2713]" + ConsoleColors.RESET;
            case WARN -> ConsoleColors.YELLOW + ConsoleColors.BOLD + "[!]" + ConsoleColors.RESET;
            case ERROR -> ConsoleColors.RED + ConsoleColors.BOLD + "[\u2717]" + ConsoleColors.RESET;
            case SKIP -> ConsoleColors.GRAY + ConsoleColors.BOLD + "[-]" + ConsoleColors.RESET;
        };
    }

    private String getDetailColor(Status status) {
        return switch (status) {
            case OK -> ConsoleColors.GREEN;
            case WARN -> ConsoleColors.YELLOW;
            case ERROR -> ConsoleColors.RED;
            case SKIP -> ConsoleColors.GRAY;
        };
    }

    public enum Status {
        OK, WARN, ERROR, SKIP
    }

    public static class Step {
        private final String name;
        private final long startMs;
        private long elapsed = 0;
        private Status status = Status.OK;
        private String detail = null;

        Step(String name) {
            this.name = name;
            this.startMs = System.currentTimeMillis();
        }

        void finish() {
            this.elapsed = System.currentTimeMillis() - this.startMs;
        }
    }
}
