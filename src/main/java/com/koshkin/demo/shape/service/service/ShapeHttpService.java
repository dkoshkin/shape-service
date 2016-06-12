package com.koshkin.demo.shape.service.service;

import com.koshkin.demo.shape.service.dto.request.RectangleDto;
import com.koshkin.demo.shape.service.dto.request.ShapeRequest;
import com.koshkin.demo.shape.service.dto.response.ResponseData;
import com.koshkin.demo.shape.service.dto.response.ShapeResponseData;
import com.koshkin.demo.shape.service.exception.UnknownStateException;
import com.koshkin.demo.shape.service.model.Rectangle;
import com.koshkin.demo.shape.service.model.Relationship;
import com.koshkin.demo.shape.service.model.Shape;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static com.koshkin.demo.shape.service.logging.LogMessages.INVALID_REQUEST_LENGTH;
import static com.koshkin.demo.shape.service.logging.LogMessages.UNKNOWN_RELATIONSHIP;

/**
 * Created by dkoshkin on 6/11/16.
 */

@Service
public class ShapeHttpService {

    private static Logger log = Logger.getLogger(ShapeHttpService.class.getName());

    @Autowired
    private ShapeBuilderService builderService;
    @Autowired
    private ShapeService shapeService;

    public ResponseData getResponseData(ShapeRequest request) throws IllegalArgumentException, UnknownStateException {
        // Request requires exactly 2 rectangles
        // TODO support multiple shapes
        if(request == null || CollectionUtils.size(request.getRectangles()) != 2) {
            log.warning(INVALID_REQUEST_LENGTH);
            throw new IllegalArgumentException();
        }

        Shape shape1 = buildShape(request.getRectangles().get(0));
        Shape shape2 = buildShape(request.getRectangles().get(1));

        Relationship relationship = shapeService.getRelationship(shape1, shape2);
        if(relationship.compareTo(Relationship.UNDEFINED) == 0) {
            log.warning(UNKNOWN_RELATIONSHIP);
            throw new UnknownStateException();
        }

        ShapeResponseData responseData = new ShapeResponseData();
        responseData.setRelationship(relationship.name());

        return responseData;
    }

    private Shape buildShape(RectangleDto rectangleDto) {
        return builderService.buildShape(rectangleDto.getX(), rectangleDto.getY(), rectangleDto.getWidth(), rectangleDto.getHeight());
    }
}
