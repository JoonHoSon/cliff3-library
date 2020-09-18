package net.cliff3.maven.common.web;

import freemarker.cache.ClassTemplateLoader;

/**
 * FreemarkerTemplateLoaderForCliff3
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class FreemarkerTemplateLoaderForCliff3 extends ClassTemplateLoader {
    public FreemarkerTemplateLoaderForCliff3() {
        super(FreemarkerTemplateLoaderForCliff3.class, "/");
    }
}
