package dataProcessor;

import commitAnalyzer.analyzer.git.fileDiff.Diff;
import commitAnalyzer.analyzer.git.git.GitAnalyzer;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.util.List;

/*
SingleExtractor用于挖掘只有一处修改地代码变更
 */
public class SingleExtractor {
    public GitAnalyzer analyzer;
    public String path = "";

    public void extract() {
        List<RevCommit> commits = analyzer.getCommits();
        for (RevCommit commit: commits) {
            Diff diff = new Diff(analyzer, commit);
        }



    }



}
