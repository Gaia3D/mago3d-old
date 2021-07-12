package gaia3d.weather.json;

import gaia3d.weather.wind.domain.WindVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
public class Bands {

    // red channel
    private final Band r;

    // green channel
    private final Band g;

    // blue channel
    private final Band b;

    /**
     * U,V,W 별 채널 리스트 맵을 R,G,B 채널의 밴드 리스트로 변경
     * @param channelMap key : WindVariable, value : List<Channel> 인 Map
     * @return band(r,g,b) list
     */
    public static List<Bands> mapToList(Map<WindVariable, List<Band>> channelMap) {
        // init bands
        List<Band> uBands = new ArrayList<>();
        List<Band> vBands = new ArrayList<>();
        List<Band> wBands = new ArrayList<>();
        // get bands
        if (channelMap.containsKey(WindVariable.U)) uBands = channelMap.get(WindVariable.U);
        if (channelMap.containsKey(WindVariable.V)) vBands = channelMap.get(WindVariable.V);
        if (channelMap.containsKey(WindVariable.W)) wBands = channelMap.get(WindVariable.W);
        // validate arguments
        int size = uBands.size();
        if (size != vBands.size()) throw new IllegalArgumentException("U, V 밴드의 길이가 같지 않습니다.");
        // merge bands
        return merge(uBands, vBands, wBands, size);
    }

    /**
     * U,V,W 별 채널 리스트를 R,G,B 채널의 밴드 리스트로 합침
     * @param uBands u bands
     * @param vBands v bands
     * @param wBands w bands
     * @param size 크기
     * @return merged band list
     */
    private static List<Bands> merge(List<Band> uBands, List<Band> vBands, List<Band> wBands, int size) {
        List<Bands> bands = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            Band rChannel = uBands.get(index);           // (red = u)
            Band gChannel = vBands.get(index);           // (green = v)
            Band bChannel = checkChannel(wBands, index); // (blue = w)
            bands.add(Bands.builder()
                    // build (r, g, b) channel
                    .r(rChannel).g(gChannel).b(bChannel)
                    .build());
        }
        return bands;
    }

    /**
     * 밴드값이 없을 경우(bands.size == 0) 디폴트 채널 값으로 설정
     * @param bands 밴드
     * @param index 인덱스
     * @return 채널 값
     */
    private static Band checkChannel(List<Band> bands, int index) {
        Band channel = Band.builder().build();
        if (bands.size() > 0) {
            channel = bands.get(index);
        }
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bands band = (Bands) o;
        return band.getR().equals(r) && band.getG().equals(g) && band.getB().equals(b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}