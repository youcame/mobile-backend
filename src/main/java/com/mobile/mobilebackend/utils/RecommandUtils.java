package com.mobile.mobilebackend.utils;

import java.util.List;
import java.util.Objects;

public class RecommandUtils {

    public static int minTagDistance(List<String> word1, List<String> word2) {
        int n1=word1.size();
        int n2=word2.size();
        int[][] dp = new int[n1+1][n2+1];
        for(int i=0;i<=n1;i++)dp[i][0]=i;
        for(int i=0;i<=n2;i++)dp[0][i]=i;
        for(int i=1;i<=n1;i++){
            for(int j=1;j<=n2;j++){
                if(!Objects.equals(word1.get(i - 1), word2.get(i - 1)))dp[i][j]=Math.min(dp[i-1][j-1],Math.min(dp[i-1][j],dp[i][j-1]))+1;
                else dp[i][j]=dp[i-1][j-1];
            }
        }
        return dp[n1][n2];
    }

    public static int minDistance(String word1, String word2) {
        int n1=word1.length();
        int n2=word2.length();
        int[][] dp = new int[n1+1][n2+1];
        char[] wordArray1 = word1.toCharArray();
        char[] wordArray2 = word2.toCharArray();
        for(int i=0;i<=n1;i++)dp[i][0]=i;
        for(int i=0;i<=n2;i++)dp[0][i]=i;
        for(int i=1;i<=n1;i++){
            for(int j=1;j<=n2;j++){
                if(word1.charAt(i-1)!=word2.charAt(j-1))dp[i][j]=Math.min(dp[i-1][j-1],Math.min(dp[i-1][j],dp[i][j-1]))+1;
                else dp[i][j]=dp[i-1][j-1];
            }
        }
        return dp[n1][n2];
    }
}
