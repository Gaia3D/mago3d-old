package gaia3d.geospatial;

import lombok.extern.slf4j.Slf4j;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 * Shape file 관련 유틸 
 *
 */
@Slf4j
public class ShapeFileParser {

    // shapefile 경로
    private final String filePath;

    public ShapeFileParser(String filePath) {
        this.filePath = filePath;
    }

//    /**
//     * shape 파일로 부터 extrusion model 시뮬레이션에 필요한 필수 컬럼(design layer) 과 옵션 컬럼 값을 추출
//     * @param extrusionColumns
//     * @return
//     * @throws ParseException
//     */
//    public List<DesignLayer> getExtrusionModelList(ObjectMapper objectMapper, String extrusionColumns) throws ParseException {
//        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ extrusionColumns = {}", extrusionColumns);
//        List<DesignLayer> extrusionModelList = new ArrayList<>();
//        if(ObjectUtils.isEmpty(extrusionColumns)) return extrusionModelList;
//
//        List<String> columnList = Arrays.asList(extrusionColumns.trim().toLowerCase().split(","));
//        try {
//            ShapefileDataStore shpDataStore = new ShapefileDataStore(new File(filePath).toURI().toURL());
//
//            shpDataStore.setCharset(StandardCharsets.UTF_8);
//            String typeName = shpDataStore.getTypeNames()[0];
//            FeatureSource<SimpleFeatureType, SimpleFeature> shapeFeatureSource = shpDataStore.getFeatureSource(typeName);
//            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = shapeFeatureSource.getFeatures();
//            FeatureIterator<SimpleFeature> features = collection.features();
//
//            String unit = this.getUnitName(shapeFeatureSource);
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@ unit = {}", unit);
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@ features = {}", features);
//            // 이게 한 row 같음
//            while (features.hasNext()) {
//                Map<String, String> attributesMap = new HashMap<>();
//
//                SimpleFeature feature = features.next();
//
//                DesignLayer designLayer = new DesignLayer();
//                log.info("@@@@@@@@@@@@@@@@@@@@@@@ feature = {}", feature);
//                // 한 row의 속성들
//                for (Property attribute : feature.getProperties()) {
//                    String attributeName = String.valueOf(attribute.getName()).toLowerCase();
//                    log.info("@@@@@@@@@@@@@@@@@@@@@@@ attributeName = {}", attributeName);
//                    if(columnList.contains(attributeName)) {
//                        // 필수 속성값일 경우
//                        if(attributeName.equalsIgnoreCase(DesignLayer.RequiredColumn.THE_GEOM.getValue())) {
//                        	Geometry geom = (Geometry) feature.getDefaultGeometry();
//
//                        	geom = DouglasPeuckerSimplifier.simplify(geom, (unit.equals("Degree Angle") ? 0.00001 : 0.1));
//                    		log.info("@@@@@@@@@@@@@@@@@@@@@@@ geomtype = {}", geom.getGeometryType());
//                        	if(geom instanceof Polygon) {
//                        		Polygon[] polygonArray = new Polygon[1];
//                        		polygonArray[0] = (Polygon) geom;
//                        		geom = new MultiPolygon(polygonArray, new GeometryFactory());
//                        	}
//
//                            designLayer.setTheGeom(geom.toText());
//                        } else if(attributeName.equalsIgnoreCase(DesignLayer.RequiredColumn.SHAPE_ID.getValue())) {
//                            designLayer.setShapeId(Long.parseLong(attribute.getValue().toString()));
//                        }
//                    } else {
//                        // 옵션 속성 값일 경우 json 통으로 넣음.
//                        if(attribute.getValue() != null) {
//                            attributesMap.put(attributeName, attribute.getValue().toString());
//                        }
//                    }
//                }
//
//                designLayer.setAttributes(objectMapper.writeValueAsString(attributesMap));
//                extrusionModelList.add(designLayer);
//            }
//            features.close();
//        } catch (IOException e) {
//            LogMessageSupport.printMessage(e);
//        }
//
//        return extrusionModelList;
//    }
//
//    /**
//     * shape 파일의 coordinate system 유닛 획듯
//     * @param featureSource
//     * @return
//     * @throws ?
//     */
//    private String getUnitName(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) {
//        CoordinateSystem cs = this.getCoordinateSystem(featureSource);
//        CoordinateSystemAxis csa0 = cs.getAxis(0);
//        Unit<?> u0 = csa0.getUnit();
//        String name = u0.getName();
//        if(name == null) name = "";
//    	return name;
//    }
    
    /**
     * shape 파일의 coordinate system 획듯
     * @param featureSource
     * @return
     * @throws ? 
     */
    private CoordinateSystem getCoordinateSystem(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) {
    	SimpleFeatureType schema = featureSource.getSchema();
        CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
        return sourceCRS.getCoordinateSystem();
    }

//    /**
//     * TODO 삭제 예정. 사용하지 않음
//     * shape file의 필수 칼럼 검사
//     *
//     * @return
//     */
//    public Boolean fieldValidate() {
//        DbaseFileReader reader = null;
//        boolean fieldValid = false;
//        try {
//            ShpFiles shpFile = new ShpFiles(filePath);
//            // 메타 정보만 수정하는 경우
//            if (!shpFile.exists(ShpFileType.SHP)) {
//                return true;
//            }
//            // field만 검사할 것이기 때문에 따로 인코딩은 설정하지 않음
//            reader = new DbaseFileReader(shpFile, false, Charset.defaultCharset());
//            DbaseFileHeader header = reader.getHeader();
//            int filedValidCount = 0;
//            // 필드 카운트
//            for (int iField = 0; iField < header.getNumFields(); iField++) {
//                String fieldName = header.getFieldName(iField);
//                if (ShapeFileField.findBy(fieldName) != null) filedValidCount++;
//            }
//            // 필수 칼럼이 모두 있는지 확인한 결과 리턴
//            fieldValid = filedValidCount == ShapeFileField.values().length;
//
//            reader.close();
//        } catch (MalformedURLException e) {
//            LogMessageSupport.printMessage(e, "MalformedURLException ============ {}", e.getMessage());
//        } catch (IOException e) {
//            LogMessageSupport.printMessage(e, "IOException ============== {} ", e.getMessage());
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (Exception e) {
//                    LogMessageSupport.printMessage(e, "Exception ============== {} ", e.getMessage());
//                }
//            }
//        }
//
//        return fieldValid;
//    }
}
