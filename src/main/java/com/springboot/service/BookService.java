package com.springboot.service;

import com.springboot.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class BookService {

    private final GoogleSheetsService googleSheetsService;

    @Autowired
    public BookService(GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try {
            List<List<Object>> sheetData = googleSheetsService.readData();

            for (int i = 1; i < sheetData.size(); i++) {
                List<Object> row = sheetData.get(i);
                Book book = new Book();

                book.setId(row.size() > 0 ? row.get(0).toString() : "");
                book.setAuthor(row.size() > 1 ? row.get(1).toString() : "");
                book.setTitle(row.size() > 2 ? row.get(2).toString() : "");
                book.setYear(row.size() > 3 ? row.get(3).toString() : "");
                book.setCopies(row.size() > 4 ? row.get(4).toString() : "");

                books.add(book);
            }

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        return books;
    }

    public List<Book> getBooksSortedByAuthor() {
        return getAllBooks().stream()
                .sorted(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Book> getBooksSortedByYear() {
        return getAllBooks().stream()
                .sorted(Comparator.comparingInt(book -> {
                    try {
                        return Integer.parseInt(book.getYear());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }))
                .collect(Collectors.toList());
    }

    public boolean removeBookById(String idToRemove) {
        try {
            List<List<Object>> data = googleSheetsService.readData();
            List<List<Object>> updated = new ArrayList<>();

            if (!data.isEmpty()) {
                updated.add(data.get(0));
            }

            boolean found = false;


            for (int i = 1; i < data.size(); i++) {
                List<Object> row = data.get(i);
                if (row.size() > 0 && row.get(0).toString().equals(idToRemove)) {
                    found = true;
                    continue;
                }
                updated.add(row);
            }

            if (!found) {
                System.out.println("Книга з ID " + idToRemove + " не знайдена.");
                return false;
            }


            for (int i = 1; i < updated.size(); i++) {
                List<Object> row = updated.get(i);
                if (row.size() > 0) {
                    row.set(0, String.valueOf(i));
                }
            }

            googleSheetsService.writeData(updated);
            return true;

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        return false;
    }




    public Book findByTitle(String title) {
        List<Book> books = getAllBooks();
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public void saveBook(Book bookToSave) {
        try {
            List<List<Object>> sheetData = googleSheetsService.readData();

            int newestYear = -1;
            int newestIndex = -1;


            for (int i = 1; i < sheetData.size(); i++) {
                List<Object> row = sheetData.get(i);
                if (row.size() > 2 && row.get(2).toString().equalsIgnoreCase(bookToSave.getTitle())) {
                    int year = -1;
                    try {
                        year = Integer.parseInt(row.size() > 3 ? row.get(3).toString() : "-1");
                    } catch (NumberFormatException ignored) {}

                    if (year > newestYear) {
                        newestYear = year;
                        newestIndex = i;
                    }
                }
            }

            if (newestIndex != -1) {
                List<Object> row = sheetData.get(newestIndex);
                int currentCopies = 0;
                if (row.size() > 4) {
                    try {
                        currentCopies = Integer.parseInt(row.get(4).toString());
                    } catch (NumberFormatException ignored) {}
                }
                row.set(4, String.valueOf(currentCopies + 1));
            } else {
                int nextId = getNextAvailableId(sheetData);
                List<Object> newRow = new ArrayList<>();
                newRow.add(String.valueOf(nextId));
                newRow.add(bookToSave.getAuthor());
                newRow.add(bookToSave.getTitle());
                newRow.add(bookToSave.getYear());
                newRow.add("1");

                sheetData.add(newRow);
            }

            googleSheetsService.writeData(sheetData);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }


    private int getNextAvailableId(List<List<Object>> sheetData) {
        int maxId = 0;
        for (int i = 1; i < sheetData.size(); i++) {
            List<Object> row = sheetData.get(i);
            if (row.size() > 0) {
                try {
                    int currentId = Integer.parseInt(row.get(0).toString());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return maxId + 1;
    }
}

