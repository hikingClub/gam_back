package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Document;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.entity.ViewHistory;
import com.gam.hikingclub.repository.DocumentRepository;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.ViewHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetailService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ViewHistoryRepository viewHistoryRepository;

    //동시성 해결을 위한 애노테이션
    @Transactional
    public void setDocument(Document document) throws Exception {
        Optional<Document> optionalDocument = documentRepository.findById(document.getDocId());

        if (optionalDocument.isPresent()) {
            Document existingDocument = optionalDocument.get();
            existingDocument.setViews(existingDocument.getViews() + 1);
            documentRepository.save(existingDocument);
        } else {
            document.setViews(1);
            document.setEmpathy(0);
            documentRepository.save(document);
        }
    }

    public Optional<Document> getDocumentById(String docId) {
        return documentRepository.findByDocId(docId);
    }

    public void setUserViewHistory(ViewHistory viewHistory) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(viewHistory.getSeq());
        if (optionalMember.isPresent()) {
            Integer memberSeq = viewHistory.getSeq();
            int maxRecords = 50;
            // 현재 유저의 검색 기록 수를 가져옴
            Integer searchHistoryCount = viewHistoryRepository.countBySeq(memberSeq);
            System.out.println("검색된 아이디 갯수 : " + searchHistoryCount);
            if (searchHistoryCount >= maxRecords) {
                // 오래된 기록을 삭제해야 하는 경우
                int recordsToDelete = (int) (searchHistoryCount - maxRecords + 1); // 삭제해야 할 레코드 수 계산
                System.out.println("지워야 할 갯수 : " + recordsToDelete);
                Pageable pageable = PageRequest.of(0, recordsToDelete, Sort.by(Sort.Direction.ASC, "keywordTime"));
                List<ViewHistory> oldestHistories = viewHistoryRepository.findBySeq(memberSeq, pageable);
                // 오래된 기록 삭제
                viewHistoryRepository.deleteAll(oldestHistories);
                System.out.println("지우고 난 뒤의 갯수 : " + viewHistoryRepository.countBySeq(memberSeq));
            }
            viewHistoryRepository.save(viewHistory);
        } else {
            throw new Exception("seq값이 존재하지 않는 멤버입니다.");
        }
    }
}
