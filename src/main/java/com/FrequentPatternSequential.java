package main.java.com;

import java.util.*;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class FrequentPatternSequential {


    public void solve(int testNumber, Scanner in, PrintWriter out) {

        int trnx=0;
        Map<String,Integer> totalCountAcrossTransactions = new HashMap<>();
        Map<Integer,String[]> transactionToItemArray = new HashMap<Integer,String[]>();
        Map<String, Integer> results = new HashMap<>();
        Map<Integer,Map<String,List<Map<Integer,Integer>>>> itemToTransactionAndIndex = new HashMap<>();

        while (in.hasNext()) {
            String support = in.nextLine();
            String[] itemsStr = support.split(" ");

            transactionToItemArray.put(trnx,itemsStr);

            for(int i=0;i<itemsStr.length;i++){
                String itemForTransaction = itemsStr[i];
                incrementCounts(0,totalCountAcrossTransactions, itemToTransactionAndIndex, itemForTransaction, null, trnx,
                        i);
            }
            trnx++;
        }

         Map<String, Integer> kOneFrequentItems = totalCountAcrossTransactions
                 .entrySet()
                 .stream()
                 .filter(x -> x.getValue() >= 2)
                 .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        iterateK(0,totalCountAcrossTransactions, transactionToItemArray, itemToTransactionAndIndex,kOneFrequentItems,results);


        LinkedHashMap<String, Integer> resultSet =
                results.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.<String, Integer>comparingByValue())
                                .thenComparing(Map.Entry.comparingByKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (String seq: resultSet.keySet()) {
            out.println("[" + resultSet.get(seq)+  ", '" + seq + "']");

        }

    }



    public static void iterateK(int k, Map<String, Integer> totalCountAcrossTransactions, Map<Integer, String[]> transactionToItemArray, Map<Integer,Map<String, List<Map<Integer, Integer>>>> itemToTransactionAndIndex,Map<String, Integer> kOneFrequentItems, Map<String, Integer> results) {

        Map<String, Integer> kFrequentItems = totalCountAcrossTransactions.entrySet().stream()
                .filter(x -> x.getValue() >= 2)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        if(k > 0) {
            results.putAll(kFrequentItems);
        }

        String[] frequentItems = kFrequentItems.keySet().toArray(new String[kFrequentItems.size()]);

        if (k < 5 && frequentItems.length != 0 && results.size() <= 20) {
            totalCountAcrossTransactions = new HashMap<>();

            for (int z = 0; z < frequentItems.length; z++) {
                String firstItem = frequentItems[z];
                Map<String, List<Map<Integer, Integer>>> KitemToTransactionAndIndex = itemToTransactionAndIndex.get(k);
                List<Map<Integer, Integer>> listOfTransWithItem = KitemToTransactionAndIndex.get(firstItem);

                if(listOfTransWithItem != null) {
                    for (Map<Integer, Integer> tranxWhereItIs : listOfTransWithItem) {
                        for (Integer aTranx : tranxWhereItIs.keySet()) {
                            Integer whereToStart = tranxWhereItIs.get(aTranx);
                            String[] sequenceOfTrans = transactionToItemArray.get(aTranx);

                            if (whereToStart + 1 < sequenceOfTrans.length) {
                                String secondItem = sequenceOfTrans[whereToStart + 1];
                                if (kOneFrequentItems.get(secondItem) != null) {
                                    incrementCounts(k, totalCountAcrossTransactions, itemToTransactionAndIndex, firstItem, secondItem, aTranx,
                                            whereToStart + 1);
                                }
                            }

                        }
                    }
                }
            }

            iterateK(k+1, totalCountAcrossTransactions, transactionToItemArray, itemToTransactionAndIndex,kOneFrequentItems,results);
        }

    }

    public static void incrementCounts(Integer k, Map<String, Integer> totalCountAcrossTransactions, Map<Integer,Map<String,List<Map<Integer,Integer>>>> itemToTransactionAndIndex, String firstItem, String secondItem, Integer transid, Integer indexesOfItInstn) {
        if(secondItem != null){
            firstItem = firstItem + " " + secondItem;
        }

        if(totalCountAcrossTransactions.get(firstItem) != null){
            Integer currentCount = totalCountAcrossTransactions.get(firstItem);
            currentCount++;
            totalCountAcrossTransactions.put(firstItem,currentCount);
        }else{
            totalCountAcrossTransactions.put(firstItem,1);
        }

        Map<String,List<Map<Integer,Integer>>> KitemToTransactionAndIndex = itemToTransactionAndIndex.get(k);

        if(KitemToTransactionAndIndex != null && KitemToTransactionAndIndex.get(firstItem) != null){
            List listOfThem = KitemToTransactionAndIndex.get(firstItem);
            HashMap transToIndex = new HashMap();
            transToIndex.put(transid,indexesOfItInstn);
            listOfThem.add(transToIndex);
            KitemToTransactionAndIndex.put(firstItem,listOfThem);
            itemToTransactionAndIndex.put(k,KitemToTransactionAndIndex);
        }else{
            KitemToTransactionAndIndex = new HashMap<>();
            HashMap transToIndex = new HashMap();
            ArrayList listOfThem = new ArrayList();
            listOfThem.add(transToIndex);
            transToIndex.put(transid,indexesOfItInstn);
            KitemToTransactionAndIndex.put(firstItem,listOfThem);
            itemToTransactionAndIndex.put(k,KitemToTransactionAndIndex);
        }

    }

}
