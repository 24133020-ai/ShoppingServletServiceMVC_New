package vn.iotstar.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.dao.impl.VideoDao;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;

public class VideoServiceImpl implements IVideoService {
    public IVideoDao videoDao = new VideoDao();

    @Override
    public void insert(Video video) {
        if (video.getCreatedDate() == null) {
            video.setCreatedDate(LocalDateTime.now());
        }
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) {
        videoDao.update(video);
    }

    @Override
    public void delete(String videoId) {
        try {
            videoDao.delete(videoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Video findById(String videoId) {
        return videoDao.findById(videoId);
    }

    @Override
    public List<Video> findNewest(int limit) {
        return videoDao.findNewest(limit);
    }

    @Override
    public List<Video> findByUser(int userId, int page, int pagesize) {
        return videoDao.findByUser(userId, page, pagesize);
    }

    @Override
    public List<Video> findAll(int page, int pagesize) {
        return videoDao.findAll(page, pagesize);
    }

    @Override
    public int countAll() {
        return videoDao.countAll();
    }

    @Override
    public int countByUser(int userId) {
        return videoDao.countByUser(userId);
    }
}
