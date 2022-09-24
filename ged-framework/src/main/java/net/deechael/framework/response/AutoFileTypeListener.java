package net.deechael.framework.response;

import net.deechael.framework.ContentType;
import net.deechael.framework.content.Content;
import net.deechael.framework.content.FileContent;

public class AutoFileTypeListener extends ResponseListener {

    @Override
    public void onResponse(PreparedResponse response) {
        Content content = response.getResponder().getContent();
        if (!(content instanceof FileContent))
            return;
        for (ContentType contentType : ContentType.values()) {
            if (contentType == ContentType.APPLICATION_OCTET_STREAM)
                continue;
            if (contentType.getSuffixes().length == 0)
                continue;
            boolean shouldBreak = false;
            for (String suffix : contentType.getSuffixes()) {
                if (((FileContent) content).getFile().getName().endsWith(suffix)) {
                    response.getResponder().setContentType(contentType);
                    shouldBreak = true;
                    break;
                }
            }
            if (shouldBreak)
                break;
        }
    }

}
