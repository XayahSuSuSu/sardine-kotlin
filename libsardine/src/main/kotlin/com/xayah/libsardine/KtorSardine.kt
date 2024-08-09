package com.xayah.libsardine

import java.io.IOException
import javax.xml.namespace.QName

/**
 * Imported from [lookfirst/sardine](https://github.com/lookfirst/sardine/blob/fa17c2ea707141b2c62df9c72c2c430b09801123/src/main/java/com/github/sardine/Sardine.java)
 *
 * The main interface for Sardine operations.
 *
 * @author jonstevens
 */
interface KtorSardine {
    /**
     * Gets a directory listing using WebDAV <code>PROPFIND</code>.
     *
     * @param url Path to the resource including protocol and hostname
     * @return List of resources for this URI including the parent resource itself
     * @throws IOException I/O error or HTTP response validation failure
     */
    suspend fun list(url: String): List<DavResource>

    /**
     * Gets a directory listing using WebDAV <code>PROPFIND</code>.
     *
     * @param url   Path to the resource including protocol and hostname
     * @param depth The depth to look at (use 0 for single resource, 1 for directory listing,
     *              -1 for infinite recursion)
     * @return List of resources for this URI including the parent resource itself
     * @throws IOException I/O error or HTTP response validation failure
     */
    suspend fun list(url: String, depth: Int): List<DavResource>

    /**
     * Gets a directory listing using WebDAV <code>PROPFIND</code>.
     *
     * @param url   Path to the resource including protocol and hostname
     * @param depth The depth to look at (use 0 for single resource, 1 for directory listing,
     *              -1 for infinite recursion)
     * @param props Additional properties which should be requested.
     * @return List of resources for this URI including the parent resource itself
     * @throws IOException I/O error or HTTP response validation failure
     */
    suspend fun list(url: String, depth: Int, props: Set<QName>): List<DavResource>

    /**
     * Gets a directory listing using WebDAV <code>PROPFIND</code>.
     *
     * @param url   Path to the resource including protocol and hostname
     * @param depth The depth to look at (use 0 for single resource, 1 for directory listing,
     *              -1 for infinite recursion)
     * @param allProp If allprop should be used, which can be inefficient sometimes;
     * warning: no allprop does not retrieve custom props, just the basic ones
     * @return List of resources for this URI including the parent resource itself
     * @throws IOException I/O error or HTTP response validation failure
     */
    suspend fun list(url: String, depth: Int, allProp: Boolean): List<DavResource>

    /**
     * Fetches a resource using WebDAV <code>PROPFIND</code>. Only the specified properties
     * are retrieved.
     *
     * @param url   Path to the resource including protocol and hostname
     * @param depth The depth to look at (use 0 for single resource, 1 for directory listing,
     *              -1 for infinite recursion)
     * @param props Set of properties to be requested
     * @return List of resources for this URI including the parent resource itself
     * @throws IOException I/O error or HTTP response validation failure
     */
    suspend fun propfind(url: String, depth: Int, props: Set<QName>): List<DavResource>
}