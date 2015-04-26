var express = require('express');
var router = express.Router();

/* GET team listing. */
router.get('/', function(req, res) {
  	var db = req.db;
        db.collection('teamlist').find({},{'schedule':0}).sort({'team':1}).toArray(function (err, items) {
        res.json(items);
    });
});

/* GET team listing for a single school by id... include schedule. */
router.get('/:id', function(req, res) {
  	var db = req.db;
    var id = req.params.id;
    db.collection('teamlist').findById(id,function (err, items) {
        res.json(items);
    });
});

/* GET team listing for a single school by nickname... include schedule. */
router.get('/nickname/:nickname', function(req, res) {
  	var db = req.db;
    var schoolNickname = req.params.nickname;
    db.collection('teamlist').findOne({'nickname':schoolNickname},function (err, items) {
        res.json(items);
    });
});

/* GET next game by nickname */
router.get('/:id/next', function(req, res) {
  	var db = req.db;
    var id = req.params.id;
   	db.collection('teamlist').findById(id, {schedule: {$elemMatch: { date : {$gte:new Date()}} }}, function (err, items) {
        res.json(items);
    });
});

/* GET next game by nickname */
router.get('/nickname/:nickname/next', function(req, res) {
  	var db = req.db;
    var schoolNickname = req.params.nickname;
   	db.collection('teamlist').findOne({'nickname':schoolNickname}, {schedule: {$elemMatch: { date : {$gte:new Date()}} }}, function (err, items) {
        res.json(items);
    });
});



module.exports = router;
