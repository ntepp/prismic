package com.duocode.assigment;

import org.springframework.stereotype.Component;

import io.prismic.*;

@Component
public class LinkResolver extends SimpleLinkResolver {

    public String resolve(Fragment.DocumentLink link) {
        return "/documents/" + link.getId() + "/" + link.getSlug();
    }

}
