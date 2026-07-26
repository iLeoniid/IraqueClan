package gg.leo.IraqueClan.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StartupReport {

    private static final String PREFIX = "\u00a77[\u00a7bIraqueClan\u00a77] \u00a7r";
    private static final String LINE = "\u00a77==============================";

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
        this.logger.info("\u00a7b\u00a7l  ╔═══════════════════════╗");
        this.logger.info("\u00a7b\u00a7l  ║   \u00a7f\u00a7lIraqueClan\u00a7b\u00a7l v" + version + "  \u00a7b\u00a7l ║");
        this.logger.info("\u00a7b\u00a7l  ║  \u00a77Sistema de Clãs  \u00a7b\u00a7l ║");
        this.logger.info("\u00a7b\u00a7l  ╚═══════════════════════╝");
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
        String timeStr = step.elapsed > 0 ? " \u00a78(" + step.elapsed + "ms)" : "";
        String detail = step.detail != null ? " \u00a77- " + getDetailColor(step.status) + step.detail : "";

        this.logger.info(PREFIX + icon + " \u00a7e" + step.name + timeStr + detail);
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
        this.logger.info("\u00a7e\u00a7l  Relatório de Inicialização");
        this.logger.info(LINE);
        this.logger.info("");

        for (Step step : this.steps) {
            String icon = getColoredIcon(step.status);
            String nameColor = step.status == Status.ERROR ? "\u00a7c" :
                    step.status == Status.WARN ? "\u00a7e" :
                            step.status == Status.SKIP ? "\u00a78" : "\u00a7a";
            this.logger.info(PREFIX + "  " + icon + " " + nameColor + step.name);
        }

        this.logger.info("");
        this.logger.info(PREFIX + "\u00a7e\u00a7l Resumo:");
        this.logger.info(PREFIX + "  \u00a77Steps: \u00a7a" + loaded + "\u00a77/\u00a7f" + this.steps.size() + " \u00a7acarregados");
        if (this.errors > 0) {
            this.logger.info(PREFIX + "  \u00a77Erros: \u00a7c" + this.errors);
        }
        if (this.warnings > 0) {
            this.logger.info(PREFIX + "  \u00a77Avisos: \u00a7e" + this.warnings);
        }
        if (this.skipped > 0) {
            this.logger.info(PREFIX + "  \u00a77Pulados: \u00a78" + this.skipped);
        }
        this.logger.info(PREFIX + "  \u00a77Tempo: \u00a7f" + totalMs + "ms");

        this.logger.info("");
        if (this.errors == 0) {
            this.logger.info(PREFIX + "\u00a7a\u00a7l  ✓ Plugin habilitado com sucesso! \u00a7aNenhum erro detectado.");
        } else {
            this.logger.info(PREFIX + "\u00a7c\u00a7l  ✗ Plugin habilitado com \u00a7c" + this.errors + " erro(s). \u00a7cVerifique os logs acima.");
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
            case OK -> "\u00a7a\u00a7l[✓]\u00a7r";
            case WARN -> "\u00a7e\u00a7l[!]\u00a7r";
            case ERROR -> "\u00a7c\u00a7l[✗]\u00a7r";
            case SKIP -> "\u00a78\u00a7l[-]\u00a7r";
        };
    }

    private String getDetailColor(Status status) {
        return switch (status) {
            case OK -> "\u00a7a";
            case WARN -> "\u00a7e";
            case ERROR -> "\u00a7c";
            case SKIP -> "\u00a78";
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
