package parrhesia1000.event;

import parrhesia1000.AuthorCache;
import parrhesia1000.dto.Author;

public record NewAuthorEvent(AuthorCache authorCache, Author author) {

}
