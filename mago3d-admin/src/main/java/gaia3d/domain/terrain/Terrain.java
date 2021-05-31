package gaia3d.domain.terrain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terrain {
    // 고유번호
    private Integer terrainId;
    // 이름
    private String terrainName;
    // Terrain 유형. geoserver, cesium-default, cesium-ion-default, cesium-ion-cdn : 우리 dem 을 업로딩, cesium-customer : cesium docker provier
    private String terrainType;
    // geoserver terrainprovider 로 사용할 레이어명
    private String geoserverTerrainproviderLayerName;
    // url
    private String url;
    // 아이콘 경로
    private String iconPath;
    // 기본 Terrain(여러개중 1개만 true). true : 활성화, false : 비활성화
    private Boolean basic;

    // 나열 순서
    private Integer viewOrder;
    // 설명
    private String description;

    // 등록일
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;
}
