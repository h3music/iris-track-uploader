package website.woocommerce;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main interface for icoderman.WooCommerce REST API
 */
public interface WooCommerce {

    /**
     * Creates icoderman.WooCommerce entity
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param object       Map with entity properties and values
     * @return Map with created entity
     */
    Map create(String endpointBase, Map<String, Object> object);

    /**
     * Retrieves on icoderman.WooCommerce entity
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param id           id of icoderman.WooCommerce entity
     * @return Retrieved icoderman.WooCommerce entity
     */
    Map get(String endpointBase, int id);

    /**
     * Retrieves all icoderman.WooCommerce entities with request parameters
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param params additional request params
     * @return List of retrieved entities
     */
    List getAll(String endpointBase, Map<String, String> params);

    /**
     * Retrieves all icoderman.WooCommerce entities
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @return List of retrieved entities
     */
    default List getAll(String endpointBase) {
        return getAll(endpointBase, Collections.emptyMap());
    }

    /**
     * Updates icoderman.WooCommerce entity
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param id           id of the entity to update
     * @param object       Map with updated properties
     * @return updated icoderman.WooCommerce entity
     */
    Map update(String endpointBase, int id, Map<String, Object> object);

    /**
     * Deletes icoderman.WooCommerce entity
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param id           id of the entity to update
     * @return deleted icoderman.WooCommerce entity
     */
    Map delete(String endpointBase, int id);

    /**
     * Makes batch operations on icoderman.WooCommerce entities
     *
     * @param endpointBase API endpoint base @see icoderman.EndpointBaseType
     * @param object       Map with lists of entities
     * @return response Map with icoderman.WooCommerce entities implicated
     */
    Map batch(String endpointBase, Map<String, Object> object);

}
