package gaia3d.rule;

public enum DataLibraryGroupType {
    // 나무
    TREE;

    public static boolean contains(String test) {
        for(DataLibraryGroupType dataLibraryGroupType : DataLibraryGroupType.values()) {
            if(dataLibraryGroupType.name().equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }
}
