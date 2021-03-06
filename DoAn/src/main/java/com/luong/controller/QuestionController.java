package com.luong.controller;

import com.luong.model.*;
import com.luong.model.DTO.QuestionDTO;
import com.luong.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    Topic_QuestionService topic_questionService;

    @Autowired
    Vote_QuestionService vote_questionService;
    @Autowired
    AnswerService answerService;

    // lay ra 1 list cau hoi
    @RequestMapping(value = "/getAllQuestion", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public List<QuestionDTO> getListQuestion() {
        Map<Integer,List> map = topic_questionService.topicsOfQuestion();

        return questionService.listQuestion();

    }
    //tra ve trang chu
    @RequestMapping(value = "/")
    public String getListOfQuestion() {
        return "listOfQuestion";

    }
    //chuyen den trang tao cau hoi
    @RequestMapping(value = "/createQuestion", method = RequestMethod.GET, headers = "Accept=Application/json")
    public String createQuestion() {
        return "createQuestion";

    }
    //tao cau hoi
    @RequestMapping(value = "/createQuestion/{name}", method = RequestMethod.POST, headers = "Accept=Application/json;charset=UTF-8")
    public
    @ResponseBody
    Question createQuestion1(@RequestBody Question question, @PathVariable("name") String name, Principal principal) {
        if(principal!=null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            questionService.add(question, user);
            Topic_Qestion topic_qestion = new Topic_Qestion();
            topic_questionService.add(topic_qestion, name, question);
        }
        return question;
    }
//    @RequestMapping(value = "/createQuestion/{name}", method = RequestMethod.POST, headers = "Accept=Application/json;charset=UTF-8")
//    public
//    @ResponseBody
//    Question createQuestion1(@RequestBody Question question, @PathVariable("name") String name, @RequestParam("file")MultipartFile file, HttpServletRequest request, Principal principal) {
////        try {
////            InputStream inputStream = file.getInputStream();
////            if (inputStream == null)
////                System.out.println("File inputstream is null");
////            // cach 2 - upload vao thu muc
////            String path = request.getSession().getServletContext().getRealPath("/") + "resources/upload/";
////            FileUtils.forceMkdir(new File(path));
////            File upload = new File (path + file.getOriginalFilename());
////            file.transferTo(upload);
////            String imagePath = request.getContextPath() + "/resources/upload/" + file.getOriginalFilename();
////            question.setImage(imagePath);
////            IOUtils.closeQuietly(inputStream);
////        } catch (IOException ex) {
////            System.out.println("Error saving uploaded file");
////        }
////
////
////
////        if(principal!=null) {
////           String email = principal.getName();
////           User user = userService.findByEmail(email);
////           questionService.add(question, user);
////           System.out.println(question.getImage());
////           Topic_Qestion topic_qestion = new Topic_Qestion();
////           topic_questionService.add(topic_qestion, name, question);
////
////       }
//        if(principal!=null) {
//            String email = principal.getName();
//            User user = userService.findByEmail(email);
//            questionService.add(question, user);
//            Topic_Qestion topic_qestion = new Topic_Qestion();
//            topic_questionService.add(topic_qestion, name, question);
//        }
//        return question;
//    }

    //hien thi chi tiet cau hoi va vote cho cau hoi
    @RequestMapping(value = "/question/{id}", method= RequestMethod.GET)
    public String answer(@PathVariable(value="id") int  idQuestion , Model model,Principal principal) {
        User user = new User();
        user.setEmail("x");
        user.setName("x");
        model.addAttribute("isAdmin",0);
        if (principal != null) {
            String email = principal.getName();
            user = userService.findByEmail(email);
            model.addAttribute("isAdmin",userService.isAdmin(user));
        }
        model.addAttribute("userlogin",user);
        model.addAttribute("question", questionService.findById(idQuestion));
        model.addAttribute("up_vote_question", vote_questionService.countUp(idQuestion));
        model.addAttribute("down_vote_question", vote_questionService.countDown(idQuestion));
        Vote_Question vote_question = new Vote_Question();
        vote_question.setDownvote(0);
        vote_question.setUpvote(0);
        if (vote_questionService.find(user, idQuestion) != null) {
            vote_question = vote_questionService.find(user, idQuestion);
            if (vote_question.getDownvote() ==1) model.addAttribute("voted", 2);//da down
            if (vote_question.getUpvote() ==1) model.addAttribute("voted", 1);//da up
        }
        else model.addAttribute("voted",0);//chua vote or chua dang nhap

        return "question";

    }


   //chuc nang search
    @RequestMapping(value = "/search/{string}",method = RequestMethod.GET, headers = "Accept=Application/json")
    public String search(@PathVariable("string") String string,Model model){
        model.addAttribute("string",string);
        return "search";
    }

    @RequestMapping(value = "/checksearchQuestion/{string}",method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public Integer checksearchQuestion(@PathVariable("string") String string){
        if(!questionService.search(string).isEmpty()){
            return 1;
        }
        else{ return 2;}
    }

    @RequestMapping(value = "/listsearchQuestion/{string}",method = RequestMethod.GET, headers = "Accept=Application/json")

    @ResponseBody

    public List<Question>listsearchQuestion(@PathVariable("string") String string){
        System.out.println(string);
        if(!questionService.search(string).isEmpty()){
        return questionService.search(string);}
        else return null;
    }
   //lay ra tat ca question theo topic tuong ung
    @RequestMapping(value = "/getAllQuestionByidTopic/{id}", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public List<Question> getnametopic(@PathVariable("id") int id){
        return topic_questionService.findQuestionByTopic(id);
    }
    // lay ra tat ca question theo user
    @RequestMapping(value = "/getAllQuestionByUser/{id}", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public List<Question> getQuestionByUser(@PathVariable("id") int id){
        User user = userService.findById(id);
        List<Question> q = new ArrayList<Question>(user.getQuestions()) ;
       return q;
    }

    // lay ra tat ca topic theo question
    @RequestMapping(value = "/getAllTopicByQuestion/{id}", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public List<Topic> getAllTopicByQuestion(@PathVariable("id") int id){
        return topic_questionService.findTopicByQuestion(id);
    }
    // lay ra tat ca topic theo question
    @RequestMapping(value = "/getAllTopicByQuestion", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public Map<Integer,List> getAllTopicByQuestion(){
        return topic_questionService.topicsOfQuestion();
    }

    //ngay 17/4
    // update question
    //26-4 : edit Question
    @RequestMapping(value = "/updatequestion", method= RequestMethod.PUT)
    @ResponseBody
    public void updatequestion(@RequestBody Question question) {
        questionService.update(question);

    }

    @RequestMapping(value = "/getmapvoteanswers/{id}", method = RequestMethod.GET, headers = "Accept=Application/json")
    @ResponseBody
    public Map<Integer, Map<String, Long>> getmapvoteanswers(@PathVariable("id") int idQuestion , Principal principal) {
        User user = new User();
        if (principal != null) {
            String email = principal.getName();
            user = userService.findByEmail(email);
        }
        return answerService.voteAnswerData(user, idQuestion);
    }

    //28-4 delQues
    @RequestMapping(value = "/delquestion/{id}", method= RequestMethod.DELETE)
    @ResponseBody
    public void delquestion(@PathVariable(value="id") int  idQuestion) {
        questionService.del(idQuestion);
        System.out.println(idQuestion);
    }

}
