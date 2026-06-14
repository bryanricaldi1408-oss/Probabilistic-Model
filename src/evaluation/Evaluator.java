package evaluation;

import retrieval.SearchResult;

import java.util.*;

/**
 * Kelas untuk mengevaluasi hasil pencarian Information Retrieval.
 * Metrik yang diimplementasikan:
 * - Precision
 * - Recall
 * - F1-Score
 * - Precision@K (untuk beberapa nilai K)
 * - 11-Point Interpolated Average Precision
 * - Mean Average Precision (MAP)
 */
public class Evaluator {

    // Nilai K yang digunakan untuk Precision@K
    private static final int[] K_VALUES = {1, 3, 5, 10, 20};

    // 11 recall levels standar untuk interpolated precision
    private static final double[] RECALL_LEVELS = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    /**
     * Mengevaluasi hasil pencarian untuk satu query.
     */
    public void evaluateSingleQuery(List<SearchResult> results, Set<Integer> relevantDocs, int queryId) {

        if (relevantDocs == null || relevantDocs.isEmpty()) {
            System.out.println("\n--- EVALUASI QUERY " + queryId + " ---");
            System.out.println("Tidak ada data relevansi tersedia.");
            return;
        }

        int totalRelevant = relevantDocs.size();
        int totalRetrieved = results.size();

        // Hitung TP, FP, FN
        int tp = 0;
        for (SearchResult r : results) {
            if (relevantDocs.contains(r.getDocId())) tp++;
        }
        int fp = totalRetrieved - tp;
        int fn = totalRelevant - tp;

        double precision = totalRetrieved > 0 ? (double) tp / totalRetrieved : 0.0;
        double recall = totalRelevant > 0 ? (double) tp / totalRelevant : 0.0;
        double f1 = (precision + recall > 0) ? 2 * precision * recall / (precision + recall) : 0.0;

        System.out.println("\n=== EVALUASI QUERY " + queryId + " ===");

        // Confusion Matrix
        System.out.println("\nConfusion Matrix:");
        System.out.println("  TP = " + tp + "  FP = " + fp);
        System.out.println("  FN = " + fn);

        // Precision, Recall, F1
        System.out.println("\nMetrik Dasar:");
        System.out.printf("  Precision = %.4f  (%d/%d)%n", precision, tp, totalRetrieved);
        System.out.printf("  Recall    = %.4f  (%d/%d)%n", recall, tp, totalRelevant);
        System.out.printf("  F1-Score  = %.4f%n", f1);

        // Precision@K
        System.out.println("\nPrecision@K:");
        for (int k : K_VALUES) {
            int relevantAtK = 0;
            int limit = Math.min(k, results.size());
            for (int i = 0; i < limit; i++) {
                if (relevantDocs.contains(results.get(i).getDocId())) relevantAtK++;
            }
            double pAtK = limit > 0 ? (double) relevantAtK / k : 0.0;
            System.out.printf("  P@%-2d = %.4f  (%d relevan dari top-%d)%n", k, pAtK, relevantAtK, limit);
        }

        // Detail Precision-Recall per rank
        System.out.println("\nDetail Precision-Recall per Peringkat (top-20):");
        int relevantFound = 0;
        List<Double> precisionAtRecallPoints = new ArrayList<>();
        int displayLimit = Math.min(20, results.size());

        for (int i = 0; i < results.size(); i++) {
            boolean isRelevant = relevantDocs.contains(results.get(i).getDocId());
            if (isRelevant) {
                relevantFound++;
                double pAtRank = (double) relevantFound / (i + 1);
                double rAtRank = (double) relevantFound / totalRelevant;
                precisionAtRecallPoints.add(pAtRank);
                if (i < displayLimit) {
                    System.out.printf("  Rank %-3d  [V]  R=%.4f  P=%.4f%n", (i + 1), rAtRank, pAtRank);
                }
            } else {
                if (i < displayLimit) {
                    System.out.printf("  Rank %-3d  [X]%n", (i + 1));
                }
            }
        }

        // Average Precision
        double ap = 0.0;
        if (!precisionAtRecallPoints.isEmpty()) {
            for (double p : precisionAtRecallPoints) ap += p;
            ap /= totalRelevant;
        }
        System.out.printf("\nAverage Precision (AP) = %.4f%n", ap);

        // 11-Point Interpolated Average Precision
        double[] interpolated = compute11PointInterpolation(results, relevantDocs, totalRelevant);
        double sum11 = 0.0;

        System.out.println("\n11-Point Interpolated Average Precision:");
        for (int i = 0; i < RECALL_LEVELS.length; i++) {
            System.out.printf("  Recall %.1f -> Precision %.4f%n", RECALL_LEVELS[i], interpolated[i]);
            sum11 += interpolated[i];
        }
        System.out.printf("  11-Point Average = %.4f%n", sum11 / 11.0);
    }

    /**
     * Menghitung 11-Point Interpolated Precision.
     */
    private double[] compute11PointInterpolation(List<SearchResult> results,
                                                  Set<Integer> relevantDocs,
                                                  int totalRelevant) {

        List<double[]> prPairs = new ArrayList<>();
        int relevantFound = 0;

        for (int i = 0; i < results.size(); i++) {
            if (relevantDocs.contains(results.get(i).getDocId())) {
                relevantFound++;
                double r = (double) relevantFound / totalRelevant;
                double p = (double) relevantFound / (i + 1);
                prPairs.add(new double[]{r, p});
            }
        }

        double[] interpolated = new double[11];
        for (int i = 0; i < RECALL_LEVELS.length; i++) {
            double level = RECALL_LEVELS[i];
            double maxPrecision = 0.0;
            for (double[] pair : prPairs) {
                if (pair[0] >= level) {
                    maxPrecision = Math.max(maxPrecision, pair[1]);
                }
            }
            interpolated[i] = maxPrecision;
        }

        return interpolated;
    }

    /**
     * Mengevaluasi semua query dan menghitung MAP serta rata-rata metrik.
     */
    public void evaluateAll(Map<Integer, List<SearchResult>> allResults,
                            Map<Integer, Set<Integer>> relevanceMap) {

        List<Double> apList = new ArrayList<>();
        double[][] all11Points = new double[allResults.size()][11];
        int queryCount = 0;

        Map<Integer, List<Double>> allPrecisionAtK = new LinkedHashMap<>();
        for (int k : K_VALUES) allPrecisionAtK.put(k, new ArrayList<>());

        double totalPrecision = 0.0, totalRecall = 0.0, totalF1 = 0.0;

        List<Integer> sortedQueryIds = new ArrayList<>(allResults.keySet());
        Collections.sort(sortedQueryIds);

        for (int queryId : sortedQueryIds) {
            List<SearchResult> results = allResults.get(queryId);
            Set<Integer> relevantDocs = relevanceMap.getOrDefault(queryId, new HashSet<>());
            if (relevantDocs.isEmpty()) continue;

            int totalRelevant = relevantDocs.size();
            int totalRetrieved = results.size();

            int tp = 0;
            for (SearchResult r : results) {
                if (relevantDocs.contains(r.getDocId())) tp++;
            }

            double precision = totalRetrieved > 0 ? (double) tp / totalRetrieved : 0.0;
            double recall = totalRelevant > 0 ? (double) tp / totalRelevant : 0.0;
            double f1 = (precision + recall > 0) ? 2 * precision * recall / (precision + recall) : 0.0;

            totalPrecision += precision;
            totalRecall += recall;
            totalF1 += f1;

            double ap = computeAP(results, relevantDocs, totalRelevant);
            apList.add(ap);

            double[] ip = compute11PointInterpolation(results, relevantDocs, totalRelevant);
            all11Points[queryCount] = ip;

            for (int k : K_VALUES) {
                int relAtK = 0;
                int limit = Math.min(k, results.size());
                for (int i = 0; i < limit; i++) {
                    if (relevantDocs.contains(results.get(i).getDocId())) relAtK++;
                }
                allPrecisionAtK.get(k).add(limit > 0 ? (double) relAtK / k : 0.0);
            }

            queryCount++;
        }

        if (queryCount == 0) {
            System.out.println("Tidak ada query dengan data relevansi untuk dievaluasi.");
            return;
        }

        double map = 0.0;
        for (double ap : apList) map += ap;
        map /= queryCount;

        double[] avg11Point = new double[11];
        for (int i = 0; i < 11; i++) {
            for (int q = 0; q < queryCount; q++) avg11Point[i] += all11Points[q][i];
            avg11Point[i] /= queryCount;
        }
        double avg11Total = 0.0;
        for (double v : avg11Point) avg11Total += v;
        avg11Total /= 11.0;

        // Print
        System.out.println("\n=== RINGKASAN EVALUASI — SELURUH QUERY ===");
        System.out.println("Total Query Dievaluasi: " + queryCount);

        System.out.println("\nRata-rata Metrik Dasar:");
        System.out.printf("  Mean Precision = %.4f%n", totalPrecision / queryCount);
        System.out.printf("  Mean Recall    = %.4f%n", totalRecall / queryCount);
        System.out.printf("  Mean F1-Score  = %.4f%n", totalF1 / queryCount);

        System.out.printf("\nMean Average Precision (MAP) = %.4f%n", map);

        System.out.println("\nRata-rata Precision@K:");
        for (int k : K_VALUES) {
            List<Double> vals = allPrecisionAtK.get(k);
            double avg = 0.0;
            for (double v : vals) avg += v;
            avg /= vals.size();
            System.out.printf("  P@%-2d = %.4f%n", k, avg);
        }

        System.out.println("\nRata-rata 11-Point Interpolated Precision:");
        for (int i = 0; i < RECALL_LEVELS.length; i++) {
            System.out.printf("  Recall %.1f -> Precision %.4f%n", RECALL_LEVELS[i], avg11Point[i]);
        }
        System.out.printf("  11-Point Average (overall) = %.4f%n", avg11Total);
    }

    /**
     * Menghitung Average Precision untuk satu query.
     */
    private double computeAP(List<SearchResult> results, Set<Integer> relevantDocs, int totalRelevant) {
        double ap = 0.0;
        int relevantFound = 0;
        for (int i = 0; i < results.size(); i++) {
            if (relevantDocs.contains(results.get(i).getDocId())) {
                relevantFound++;
                ap += (double) relevantFound / (i + 1);
            }
        }
        return totalRelevant > 0 ? ap / totalRelevant : 0.0;
    }
}
