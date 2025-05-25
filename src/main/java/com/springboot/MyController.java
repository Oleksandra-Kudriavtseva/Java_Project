package com.springboot;

import com.springboot.model.Book;
import com.springboot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MyController {

    private final BookService bookService;

    @Autowired
    public MyController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/api/books")
    @ResponseBody
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books")
    public String showBooksPage(@RequestParam(required = false) String sort, Model model) {
        List<Book> books;

        if ("author".equals(sort)) {
            books = bookService.getBooksSortedByAuthor();
        } else if ("year".equals(sort)) {
            books = bookService.getBooksSortedByYear();
        } else {
            books = bookService.getAllBooks();
        }

        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/writeoff")
    public String showWriteOffForm() {
        return "writeoff";
    }

    @PostMapping("/writeoff")
    public String writeOffBook(@RequestParam("id") String id, Model model) {
        boolean success = bookService.removeBookById(id);
        if (success) {
            model.addAttribute("message", "Книгу успішно списано.");
        } else {
            model.addAttribute("message", "Книгу з таким ID не знайдено.");
        }
        return "writeoff";
    }




    @GetMapping("/addbook")
    public String showReturnBookForm() {
        return "addbook";
    }

    @PostMapping("/addbook")
    public String returnBook(
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String year,
            @RequestParam String action,
            Model model) {

        if ("check".equals(action)) {
            Book book = bookService.findByTitle(title);

            if (book != null) {
                book.setQuantity(book.getQuantity() + 1);
                bookService.saveBook(book);
                model.addAttribute("successMessage", "Книга успішно додана!");
            } else {
                model.addAttribute("notFound", true);
                model.addAttribute("title", title);
            }

        } else if ("add".equals(action)) {
            Book newBook = new Book();
            newBook.setAuthor(author);
            newBook.setTitle(title);
            newBook.setYear(year);
            newBook.setQuantity(1); // Копій 1


            bookService.saveBook(newBook);
            model.addAttribute("successMessage", "Нова книга додана успішно!");
        }

        return "addbook";
    }



}
