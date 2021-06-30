package gaia3d.weather.json;

import gaia3d.weather.wind.WindVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Band {

    private BandInfo r;
    private BandInfo g;
    private BandInfo b;

    public static List<Band> mapToList(Map<WindVariable, List<BandInfo>> bandMap) {
        List<BandInfo> uBands = bandMap.get(WindVariable.U);
        List<BandInfo> vBands = bandMap.get(WindVariable.V);
        List<Band> bands = new ArrayList<>();
        for (int i = 0; i < uBands.size(); i++) {
            bands.add(new Band(uBands.get(i), vBands.get(i), new BandInfo()));
        }
        return bands;
    }

}
