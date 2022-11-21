package com.books.app.attr.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.books.app.attr.entity.Attr;
import com.books.app.attr.repository.AttrRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttrService {
	private final AttrRepository attrRepository;

	@Transactional
	public void set(String varName, String value) {
		set(varName, value, null);
	}

	@Transactional
	public void set(String varName, String value, LocalDateTime expireDate) {
		String[] varNameBits = varName.split("__");
		String relTypeCode = varNameBits[0];
		long relId = Integer.parseInt(varNameBits[1]);
		String typeCode = varNameBits[2];
		String type2Code = varNameBits[3];

		attrRepository.upsert(relTypeCode, relId, typeCode, type2Code, value, expireDate);
	}

	public Attr findAttr(String varName) {
		String[] varNameBits = varName.split("__");
		String relTypeCode = varNameBits[0];
		long relId = Integer.parseInt(varNameBits[1]);
		String typeCode = varNameBits[2];
		String type2Code = varNameBits[3];

		return attrRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2Code(relTypeCode, relId, typeCode, type2Code).orElse(null);
	}

	public String get(String varName, String defaultValue) {
		Attr attr = findAttr(varName);

		if (attr == null) {
			return defaultValue;
		}

		if (attr.getExpireDate() != null && attr.getExpireDate().compareTo(LocalDateTime.now()) < 0) {
			return defaultValue;
		}

		return attr.getValue();
	}
}
