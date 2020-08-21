package commitAnalyzer.gumtree;

import commitAnalyzer.analyzer.histories.variation.Mutant;
import commitAnalyzer.analyzer.histories.variation.MutantType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NGramsCalculator;
import commitAnalyzer.gumtreediff.actions.ActionGenerator;
import commitAnalyzer.gumtreediff.actions.model.*;
import commitAnalyzer.gumtreediff.gen.jdt.JdtTreeGenerator;
import commitAnalyzer.gumtreediff.matchers.MappingStore;
import commitAnalyzer.gumtreediff.matchers.Matcher;
import commitAnalyzer.gumtreediff.matchers.Matchers;
import commitAnalyzer.gumtreediff.tree.ITree;
import javafx.util.Pair;
import org.eclipse.jdt.core.dom.ASTParser;
import commitAnalyzer.util.ReaderTool;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GumTree {

    public static List<Mutant> getDifference(String newContent, String oldContent) {
        return getDifference(newContent, oldContent, ASTParser.K_CLASS_BODY_DECLARATIONS);
    }

    public static List<Action> getActions(String newContent, String oldContent, int codeLevel) {
        List<Action> result = new ArrayList<>();
        try {
            JdtTreeGenerator generator = new JdtTreeGenerator();
            ITree r1 = generator.generateFromString(oldContent, codeLevel).getRoot();
            ITree r2 = generator.generateFromString(newContent, codeLevel).getRoot();
            Matcher m = Matchers.getInstance().getMatcher(r1, r2);
            m.match();
            MappingStore store = m.getMappings();
            ActionGenerator g = new ActionGenerator(r1, r2, m.getMappings());
            g.generate();
            result = g.getActions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Mutant> getDifference(String newContent, String oldContent, int codeLevel) {
        List<Mutant> result = new ArrayList<>();
        List<Action> actions = getActions(newContent, oldContent, codeLevel);

        for (Action action: actions) {
             if (action instanceof Update) {
                Mutant mutant = new Mutant(MutantType.UPDATE,
                        strip(action.getNode().getLabel()),
                        strip(((Update) action).getValue()));
                result.add(mutant);
            } else if (action instanceof Insert){
                ITree node = action.getNode();
                if (node.isLeaf()) {
                    Mutant mutant = new Mutant(MutantType.INSERT, "", strip(node.getLabel()));
                    result.add(mutant);
                }
            } else if (action instanceof Delete) {
                ITree node = action.getNode();
                if (node.isLeaf()) {
                    Mutant mutant = new Mutant(MutantType.DELETE, strip(node.getLabel()), "");
                    result.add(mutant);
                }
            } else if (action instanceof Move) {
                //System.out.println("new action type: " + action.getName());
            } else {
                System.out.println("new action type: " + action.getName());
            }
        }

        return result;
    }

    public static String strip(String s) {
        if (s.length() > 1 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }


    }
    public static void main(String[] args) {
        NGramsCalculator n = new NGramsCalculator(2);
        System.out.println(n.calculateSimilarity("", ""));

        Object oo = GumTree.getActions("int a = 2; int b = get(3);", "int a = 2; int b= get(4);", ASTParser.K_STATEMENTS);


        String content1 = ReaderTool.read("file1.java");
        String content2 = ReaderTool.read("file2.java");

        Object o = getDifference(content2, content1, ASTParser.K_STATEMENTS);
        int a = 22;
    }

}
