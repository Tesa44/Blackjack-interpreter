import blackjack.engine.RoundResult;
import blackjack.sim.FrontendExportData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoundViewMapper {
    public List<FrontendExportData.RoundView> map(List<RoundResult> allResults, List<RoundResult> selectedResults) {
        List<FrontendExportData.RoundView> roundViews = new ArrayList<>();
        Set<RoundResult> selected = new HashSet<>(selectedResults);

        for (int i = 0; i < allResults.size(); i++) {
            RoundResult roundResult = allResults.get(i);
            if (selected.contains(roundResult)) {
                roundViews.add(new FrontendExportData.RoundView(i + 1, roundResult));
            }
        }

        return roundViews;
    }
}
