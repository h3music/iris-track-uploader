package website;

import website.woocommerce.*;
import website.woocommerce.oauth.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Website {
    /**
     * Method to upload Woocommerce product through Woocommerce API
     * @param record record to provide parameters
     * @throws InterruptedException
     */
    public static void upload(WebsiteRecord record) throws InterruptedException {
        progressBar.create("Uploading to h3music.com");

        String slug = record.name().replace(" ", "").toLowerCase();

        WooCommerce wooCommerce = setupClient();

        ArrayList<Integer> categoryIds;

        categoryIds = Website.getCategoryIds(record.categoryNames());

        progressBar.update(.2);

        // Product Api Call
        Map<String, Object> productInfo = prepareProduct(record.name(),
                record.date(), record.description(), categoryIds,
                record.mp3Id());

        Map product = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), productInfo);

        int id = (int) product.get("id");

        if (!Objects.isNull(product.get("id"))) {
            progressBar.update(.4);
        } else {
            System.out.println("The Website Product could not be made");
            System.exit(1);
        }

        // Variations Api Calls
        Map<String, Object> stemInfo = prepareVariation("STEM", "29.99",
                slug + "_stem",
                "https://drive.google.com/uc?id=" + record.stemId());
        wooCommerce.create("products/"+ id + "/variations", stemInfo);
        progressBar.update(.6);

        Map<String, Object> wavInfo = prepareVariation("WAV", "9.99",
                slug + "_wav",
                "https://drive.google.com/uc?id="+ record.wavId() + "&export=download");
        wooCommerce.create("products/"+ id + "/variations", wavInfo);
        progressBar.update(.8);

        addImage(wooCommerce, id);
        progressBar.update(1);

    }

    /**
     * Method to add image to product, attempts multiple times
     * @param wooCommerce config
     * @param id id of product
     * @throws InterruptedException
     */
    private static void addImage(WooCommerce wooCommerce, int id) throws InterruptedException {
        int attempts = 0;

        while (attempts < 10) {
            Map image = wooCommerce.create("products/"+ id, prepareImage(id));

            if (!("woocommerce_product_invalid_image_id".equals(image.get("code")))) {
                break;
            }

            TimeUnit.MINUTES.sleep(1);
            attempts++;
        }

        if (attempts >= 10) {
            System.out.println("Could not add image. https://h3music.com/?post_type=product&p=" + id);
        } else {
            System.out.println("Uploaded Successfully: https://h3music.com/?post_type=product&p=" + id);
        }
    }

    /**
     * Method to add image to product (web id +6 from product)
     * @param id id of product
     * @return
     */
    public static Map<String, Object> prepareImage(int id) {

        Map<String, Object> imageInfo = new HashMap<>();

        // Image "Ambitious"
        Map<String, Object> images = new HashMap<>();
        images.put("id", (id+6));

        ArrayList<Map<String, Object>> imageArray = new ArrayList<>();
        imageArray.add(images);
        imageInfo.put("images", imageArray);

        return imageInfo;
    }

    /**
     * Gets the category ids from Strings
     * @param categoryNames names of category
     * @return ids
     */
    public static ArrayList<Integer> getCategoryIds(List<String> categoryNames) {
        ArrayList<Integer> categoryIds = new ArrayList<>();

        for (String categoryName : categoryNames) {
            int categoryId = Website.getCategories(categoryName);

            if (categoryId < 0) {
                Website.addCategory(categoryName);
                categoryId = Website.getCategories(categoryName);
            }
            categoryIds.add(categoryId);
        }
        return categoryIds;
    }

    /**
     * Get product category if from name
     * @param categoryName name of string
     * @return
     */
    private static int getCategories(String categoryName) {
        WooCommerce wooCommerce = setupClient();

        String[] nameParts = categoryName.split(" ");

        String searchName = nameParts[0].replaceAll("[^a-zA-Z0-9]", "");

        Map<String, String> params = new HashMap<>();
        params.put("search", searchName);
        List categories = wooCommerce.getAll(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(), params);

        int i = 0;

        if (categories.size() == 0) {
            return -1;
        }

        while (i < categories.size()) {
            LinkedHashMap category = (LinkedHashMap) categories.get(i);

            if (Objects.equals(category.get("name"), categoryName)) {
                return (int) category.get("id");
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * Add category to Woocommerce site through Woocommerce API
     * @param categoryName name of category
     * @return id of new category
     */
    private static int addCategory(String categoryName) {
        WooCommerce wooCommerce = setupClient();

        Map<String, Object> categoryInfo = new HashMap<>();
        categoryInfo.put("name", categoryName);

        Map category = wooCommerce.create(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(), categoryInfo);

//        int id = (int) category.get("id");

        return 0;
    }

    /**
     * Client configuration for Woocommerce (Using OAuth)
     * @return
     */
    private static WooCommerce setupClient() {
        OAuthConfig config = new OAuthConfig("",
                "",
                "");
        return new WooCommerceAPI(config, ApiVersionType.V3);
    }

    /**
     * Create Initial Woocommerce Product
     * @param name name of product
     * @param date publish date of product
     * @param description description of product
     * @param categoryIds category IDs of product
     * @param mp3Id free download id of product
     * @return
     */
    private static Map<String, Object> prepareProduct(String name, String date, String description,
                                                      ArrayList<Integer> categoryIds, String mp3Id) {
        // General Info
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("name", name);
        productInfo.put("slug", name.replace(" ", "").toLowerCase());
        productInfo.put("date_created", date);
        productInfo.put("type", "variable");
        productInfo.put("status", "future");
        productInfo.put("featured", false);
        productInfo.put("catalog_visibility", "visible");
        productInfo.put("description", description);
        productInfo.put("price", "9.99");
        productInfo.put("on_sale", false);
        productInfo.put("purchasable", true);
        productInfo.put("virtual", false);
        productInfo.put("downloadable", false);
        productInfo.put("download_limit", -1);
        productInfo.put("download_expiry", -1);
        productInfo.put("tax_status", "taxable");
        productInfo.put("manage_stock", false);
        productInfo.put("backorders", "no");
        productInfo.put("sold_individually", true);
        productInfo.put("shipping_required", true);
        productInfo.put("reviews_allowed", false);
        productInfo.put("parent_id", 0);
        productInfo.put("menu_order", 0);
        productInfo.put("stock_status", "instock");
        productInfo.put("has_options", true);

        // Categories
        ArrayList<Map<String, Object>> categoriesArray = new ArrayList<>();

        for (int id: categoryIds) {
            Map<String, Object> categories = new HashMap<>();
            categories.put("id", id);
            categoriesArray.add(categories);
        }

        productInfo.put("categories", categoriesArray);

        // Attributes
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 0);
        attributes.put("name", "File Type");
        attributes.put("position", 0);
        attributes.put("visible", true);
        attributes.put("variation", true);
        attributes.put("options", new String[]{"WAV", "STEM"});

        ArrayList<Map<String, Object>> attributesArray = new ArrayList<>();
        attributesArray.add(attributes);
        productInfo.put("attributes", attributesArray);

        // Default Attributes
        Map<String, Object> defaultAttributes = new HashMap<>();
        defaultAttributes.put("id", 0);
        defaultAttributes.put("name", "File Type");
        defaultAttributes.put("option", "WAV");

        ArrayList<Map<String, Object>> defaultAttributesArray = new ArrayList<>();
        defaultAttributesArray.add(defaultAttributes);
        productInfo.put("default_attributes", defaultAttributesArray);

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "free_download");
        metadata.put("value", mp3Id);

        ArrayList<Map<String, Object>> metadataArray = new ArrayList<>();
        metadataArray.add(metadata);
        productInfo.put("meta_data", metadataArray);

        return productInfo;
    }

    /**
     * Create Woocommerce Product Variation
     * @param fileType file type of variation
     * @param price price of variation
     * @param downloadName name of variation download
     * @param downloadLink link for variation download
     * @return
     */
    private static Map<String, Object> prepareVariation(String fileType, String price,
                                                        String downloadName, String downloadLink) {
        // General Info
        Map<String, Object> variationInfo = new HashMap<>();
        variationInfo.put("regular_price", price);
        variationInfo.put("status", "publish");
        variationInfo.put("virtual", true);
        variationInfo.put("downloadable", true);
        variationInfo.put("download_limit", -1);
        variationInfo.put("download_expiry", 1);
        variationInfo.put("tax_status", "taxable");
        variationInfo.put("manage_stock", false);
        variationInfo.put("stock_status", "instock");
        variationInfo.put("backorders", "no");
        variationInfo.put("menu_order", 1);

        // Attributes
        Map<String, Object> variationAttributes = new HashMap<>();
        variationAttributes.put("id", 0);
        variationAttributes.put("name", "File Type");
        variationAttributes.put("option", fileType);

        ArrayList<Map<String, Object>> attributesArray = new ArrayList<>();
        attributesArray.add(variationAttributes);
        variationInfo.put("attributes", attributesArray);

        // Downloads
        Map<String, Object> variationDownloads = new HashMap<>();
        variationDownloads.put("name", downloadName);
        variationDownloads.put("file", downloadLink);

        ArrayList<Map<String, Object>> downloadsArray = new ArrayList<>();
        downloadsArray.add(variationDownloads);
        variationInfo.put("downloads", downloadsArray);

        return variationInfo;
    }
}
