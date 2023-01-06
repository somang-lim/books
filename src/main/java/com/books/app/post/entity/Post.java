package com.books.app.post.entity;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.books.app.base.entity.BaseEntity;
import com.books.app.member.entity.Member;
import com.books.app.postTag.entity.PostTag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {
	private String subject;

	@Column(columnDefinition = "LONGTEXT")
	private String content;

	@Column(columnDefinition = "LONGTEXT")
	private String contentHtml;

	@ManyToOne(fetch = LAZY)
	private Member author;

	public String getForPrintContentHtml() {
		return contentHtml.replaceAll("toastui-editor-ww-code-block-highlighting", "");
	}

	public String getExtra_inputValue_hashTagContents() {
		Map<String, Object> extra = getExtra();

		if (!extra.containsKey("postTags")) {
			return "";
		}

		List<PostTag> postTags = (List<PostTag>) extra.get("postTags");

		if (postTags.isEmpty()) {
			return "";
		}

		return postTags
			.stream()
			.map(postTag -> "#" + postTag.getPostKeyword().getContent())
			.sorted()
			.collect(Collectors.joining(" "));
	}

	public String getExtra_postTagLinks() {
		Map<String, Object> extra = getExtra();

		if (!extra.containsKey("postTags")) {
			return "";
		}

		List<PostTag> postTags = (List<PostTag>) extra.get("postTags");

		if (postTags.isEmpty()) {
			return "";
		}

		return postTags
			.stream()
			.map(postTag -> {
				String text = "#" + postTag.getPostKeyword().getContent();

				return """
						<a href="%s" class="text-link">%s</a>
						"""
						.stripIndent()
						.formatted(postTag.getPostKeyword().getListUrl(), text);
			})
			.sorted()
			.collect(Collectors.joining(" "));
	}

	public String getJdenticon() {
		return "post__" + getId();
	}
}
