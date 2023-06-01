package com.books.app.postKeyword.repository;

import java.util.List;

import com.books.app.postKeyword.entity.PostKeyword;

public interface PostKeywordRepositoryCustom {
	List<PostKeyword> getQslAllByAuthorId(Long authorId);
}
