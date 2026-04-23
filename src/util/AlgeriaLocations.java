package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgeriaLocations {

    private static final Map<String, List<String>> wilayasMap = new HashMap<>();

    static {
        wilayasMap.put("01. Adrar", Arrays.asList("Adrar", "Tamest", "Charouine", "Reggane", "Tsabit"));
        wilayasMap.put("02. Chlef", Arrays.asList("Chlef", "Tenes", "El Karimia", "Ouled Fares", "Chettia"));
        wilayasMap.put("03. Laghouat", Arrays.asList("Laghouat", "Ksar El Hirane", "Aflou", "Hassi R'Mel"));
        wilayasMap.put("04. Oum El Bouaghi", Arrays.asList("Oum El Bouaghi", "Ain Beida", "Ain M'lila", "Fkirina"));
        wilayasMap.put("05. Batna", Arrays.asList("Batna", "Barika", "Merouana", "Ain Touta", "Tazoult"));
        wilayasMap.put("06. Bejaia", Arrays.asList("Bejaia", "Amizour", "Akbou", "Timezrit", "Souk El Tenine", "Tichy",
                "Aokas", "El Kseur"));
        wilayasMap.put("07. Biskra", Arrays.asList("Biskra", "Tolga", "Sidi Okba", "Ourlal"));
        wilayasMap.put("08. Bechar", Arrays.asList("Bechar", "Abadla", "Taghit", "Beni Ounif"));
        wilayasMap.put("09. Blida", Arrays.asList("Blida", "Boufarik", "El Affroun", "Ouled Yaich", "Beni Mered"));
        wilayasMap.put("10. Bouira", Arrays.asList("Bouira", "Lakhdaria", "Sour El Ghozlane", "M'Chedallah"));
        wilayasMap.put("11. Tamanrasset", Arrays.asList("Tamanrasset", "Ain Salah", "In Guezzam"));
        wilayasMap.put("12. Tebessa", Arrays.asList("Tebessa", "Bir El Ater", "Cheria", "El Kouif"));
        wilayasMap.put("13. Tlemcen", Arrays.asList("Tlemcen", "Maghnia", "Remchi", "Hennaya", "Nedroma"));
        wilayasMap.put("14. Tiaret", Arrays.asList("Tiaret", "Sougueur", "Frenda", "Mahdia"));
        wilayasMap.put("15. Tizi Ouzou",
                Arrays.asList("Tizi Ouzou", "Dra Ben Khedda", "Azazga", "Larbaa Nath Irathen", "Ouacif"));
        wilayasMap.put("16. Algiers", Arrays.asList("Alger Centre", "Sidi M'Hamed", "El Biar", "Bab El Oued", "Kouba",
                "Hussein Dey", "Bir Mourad Rais", "Hydra", "Baba Hassen", "Rouiba"));
        wilayasMap.put("17. Djelfa", Arrays.asList("Djelfa", "Ain Oussara", "Hassi Bahbah", "Messaad"));
        wilayasMap.put("18. Jijel", Arrays.asList("Jijel", "Taher", "El Milia", "Chekfa"));
        wilayasMap.put("19. Setif", Arrays.asList("Setif", "El Eulma", "Ain Azel", "Ain Oulmene", "Bougaa"));
        wilayasMap.put("20. Saida", Arrays.asList("Saida", "Ain El Hadjar", "Youb"));
        wilayasMap.put("21. Skikda", Arrays.asList("Skikda", "El Harrouch", "Collo", "Azzaba"));
        wilayasMap.put("22. Sidi Bel Abbes", Arrays.asList("Sidi Bel Abbes", "Sfisef", "Telagh", "Ben Badis"));
        wilayasMap.put("23. Annaba", Arrays.asList("Annaba", "El Bouni", "El Hadjar", "Berrahal", "Sidi Amar"));
        wilayasMap.put("24. Guelma", Arrays.asList("Guelma", "Oued Zenati", "Heliopolis"));
        wilayasMap.put("25. Constantine",
                Arrays.asList("Constantine", "El Khroub", "Hamma Bouziane", "Didouche Mourad"));
        wilayasMap.put("26. Medea", Arrays.asList("Medea", "Berrouaghia", "Ksar El Boukhari", "El Omaria"));
        wilayasMap.put("27. Mostaganem", Arrays.asList("Mostaganem", "Ain Tedles", "Bouguirat", "Sidi Ali"));
        wilayasMap.put("28. M'Sila", Arrays.asList("M'Sila", "Bou Saada", "Sidi Aissa", "Magra"));
        wilayasMap.put("29. Mascara", Arrays.asList("Mascara", "Sig", "Tighennif", "Ghriss"));
        wilayasMap.put("30. Ouargla", Arrays.asList("Ouargla", "Touggourt", "Hassi Messaoud"));
        wilayasMap.put("31. Oran", Arrays.asList("Oran", "Es Senia", "Bir El Djir", "Arzew", "Gdyel", "Ain Turk"));
        wilayasMap.put("32. El Bayadh", Arrays.asList("El Bayadh", "Bougtob", "Brezina"));
        wilayasMap.put("33. Illizi", Arrays.asList("Illizi", "Djanet", "In Amenas"));
        wilayasMap.put("34. Bordj Bou Arreridj", Arrays.asList("Bordj Bou Arreridj", "Ras El Oued", "Mansoura"));
        wilayasMap.put("35. Boumerdes", Arrays.asList("Boumerdes", "Boudouaou", "Dellys", "Thenia", "Zemmouri"));
        wilayasMap.put("36. El Tarf", Arrays.asList("El Tarf", "Drean", "El Kala", "Besbes"));
        wilayasMap.put("37. Tindouf", Arrays.asList("Tindouf", "Oum El Assel"));
        wilayasMap.put("38. Tissemsilt", Arrays.asList("Tissemsilt", "Theniet El Had", "Lardjem"));
        wilayasMap.put("39. El Oued", Arrays.asList("El Oued", "Robbah", "Guemar", "Debila"));
        wilayasMap.put("40. Khenchela", Arrays.asList("Khenchela", "Kais", "Chechar"));
        wilayasMap.put("41. Souk Ahras", Arrays.asList("Souk Ahras", "Sedrata", "Medaourouch"));
        wilayasMap.put("42. Tipaza", Arrays.asList("Tipaza", "Kolea", "Cherchell", "Bou Ismail", "Hadjout"));
        wilayasMap.put("43. Mila", Arrays.asList("Mila", "Chelghoum Laid", "Tadjenanet", "Grarem Gouga"));
        wilayasMap.put("44. Ain Defla", Arrays.asList("Ain Defla", "Khemis Miliana", "Miliana", "El Attaf"));
        wilayasMap.put("45. Naama", Arrays.asList("Naama", "Mecheria", "Ain Sefra"));
        wilayasMap.put("46. Ain Temouchent", Arrays.asList("Ain Temouchent", "Hammam Bou Hadjar", "Beni Saf"));
        wilayasMap.put("47. Ghardaia", Arrays.asList("Ghardaia", "Metlili", "El Menia", "Berriane"));
        wilayasMap.put("48. Relizane", Arrays.asList("Relizane", "Oued Rhiou", "Mazouna"));
    }

    public static String[] getWilayas() {
        return wilayasMap.keySet().stream().sorted().toArray(String[]::new);
    }

    public static List<String> getBaladiyas(String wilaya) {
        return wilayasMap.getOrDefault(wilaya, Arrays.asList());
    }
}
