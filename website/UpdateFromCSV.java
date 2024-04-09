package website;

class UpdateFromCSV {

    public static void updateSiteFromCSV(String inputPath) {
        
        List<List<String>> rows = CsvReaderSpecial.read(inputPath);
        
        for (int i=0; i < rows.size();i++) {
            int id = Integer.parseInt(rows.get(i).get(0));
            String link = rows.get(i).get(1);
            ProductUpdate.update(id, link);
        }
        
        System.out.println("Completed Updates");
    } 
}
