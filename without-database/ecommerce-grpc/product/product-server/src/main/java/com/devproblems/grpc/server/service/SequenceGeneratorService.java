package com.devproblems.grpc.server.service;

import com.devproblems.grpc.server.collection.DbSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class SequenceGeneratorService {
    @Autowired
    private MongoOperations mongoOperations;

    public int getSequenceNumber(String sequenceName) {
        //Mendapatkan id terakhir pada db
        Query query = new Query(Criteria.where("id").is(sequenceName));
        //Melakukan update pada id
        Update update = new Update().inc("seq",1);
        //Memodifikasi dokumen agar memiliki id yang sesuai
        DbSequence counter = mongoOperations
                .findAndModify(query,
                        update, options().returnNew(true).upsert(true),
                        DbSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
