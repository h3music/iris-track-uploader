package website;

import website.woocommerce.*;
import website.woocommerce.oauth.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ProductUpdate {
    /**
     * Method to upload Woocommerce product through Woocommerce API
     * @throws InterruptedException
     */
    public static void update(int id, String value) throws InterruptedException {
        progressBar.create("Updating H3 Music Product: " + id);

        WooCommerce wooCommerce = setupClient();

        // Product Api Call
        Map<String, Object> productInfo = updateProduct(value);

        Map product = wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(), id, productInfo);

        if (!Objects.isNull(product.get("id"))) {
            progressBar.update(.4);
        } else {
            System.out.println("The Website Product could not be updated: "+ id);
            System.exit(1);
        }

        progressBar.update(1);

    }

    /**
     * Client configuration for Woocommerce (Using OAuth)
     * @return
     */
    private static WooCommerce setupClient() {
        OAuthConfig config = new OAuthConfig("https://h3music.com", "", "");
        return new WooCommerceAPI(config, ApiVersionType.V3);
    }

    /**
     * Create Initial Woocommerce Product
     * @param value YouTube link of product
     * @return
     */
    private static Map<String, Object> updateProduct(String value) {
        // General Info
        Map<String, Object> productInfo = new HashMap<>();
//        productInfo.put("id", id);

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "s3_free_download");
        metadata.put("value", value);

        ArrayList<Map<String, Object>> metadataArray = new ArrayList<>();
        metadataArray.add(metadata);
        productInfo.put("meta_data", metadataArray);

        return productInfo;
    }
}

