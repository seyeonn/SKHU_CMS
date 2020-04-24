package com.sofCap.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sofCap.dto.AccountDto;
import com.sofCap.dto.FilesDto;
import com.sofCap.mapper.AccountMapper;
import com.sofCap.mapper.FileMapper;
import com.sofCap.model.SemDate;
import com.sofCap.service.AccountService;
import com.sofCap.service.ClubService;
import com.sofCap.service.FileService;
import com.sofCap.service.SemDateService;

@Controller
@RequestMapping("club_union")
public class ClubUnionController {
	@Autowired
	AccountService accountService;
	@Autowired
	ClubService clubService;
	@Autowired
	SemDateService semdateService;
	@Autowired
	FileService fileService;
	@Autowired
	FileMapper fileMapper;
	@Autowired
	AccountMapper accountMapper;

	String[] account_type = { "중앙지원금", "동아리회비" };

//	@RequestMapping("account")
//	public List<AccountDto> account() {
//		return accountService.findAll();
//	}

	// 버전 1
//	@RequestMapping("account")
//	public String account(Model model, @RequestParam(name="club_id",defaultValue="1") int club_id) {
//		List<AccountDto> accounts = accountService.findByClubId(club_id);
//		model.addAttribute("accounts", accounts);
//		model.addAttribute("clubs",clubService.findAll());
//		return "club_union/account";
//	}

	// 버전 2
//	@RequestMapping("account")
//	public String account(Model model) {
//		List<AccountDto> accounts = accountService.findAll();
//		model.addAttribute("accounts", accounts);
//		model.addAttribute("clubs",clubService.findAll());
//		return "club_union/account";
//	}

	// 버전 3
	@RequestMapping(value = "account")
	public String account(Model model, SemDate semdate) {
//		System.out.println(semdate.getSem_name());
		String sem_name = semdate.getSem_name();
		List<AccountDto> accounts = accountService.findBySem(semdate);
		List<AccountDto> totals = accountService.getTotalByClubId(sem_name);
		model.addAttribute("accounts", accounts);
		model.addAttribute("clubs", clubService.findAll());
		model.addAttribute("sems", semdateService.findAll());
		model.addAttribute("semdate", semdate);
		model.addAttribute("account_type", account_type);
		model.addAttribute("totals", totals);
		return "club_union/account";
	}

	// (club_id, price, total, remark, file_id, account_type, date
	@RequestMapping(value = "account_save", method = RequestMethod.POST)
	public String account_save(Model model, @RequestParam("club_id") int club_id, @RequestParam("price") int[] price,
			@RequestParam("remark") String[] remark, @RequestBody MultipartFile[] file,
			@RequestParam("account_type") int[] account_type,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date[] date, SemDate semdate) {
		String sem_name = semdate.getSem_name();
		save(club_id, price, remark, file, account_type, date, sem_name);
		return "redirect:account#fh5co-tab-feature-center" + club_id;
	}

	@Transactional
	private void save(int club_id, int[] price, String[] remark, MultipartFile[] file, int[] account_type, Date[] date,
			String sem_name) {
		for (int i = 0; i < price.length; ++i) {
			AccountDto account = new AccountDto();
			account.setClub_id(club_id);
			account.setPrice((int) price[i]);
//			int total = accountService.getTotalByClubId(sem_name, club_id[i]);
			account.setTotal(0); // total culmn 사용안함
			account.setRemark(remark[i]);
			if (!file[i].isEmpty()) {
				int f_id = fileService.accountFileUpload(file[i]);
				account.setFile_id((int) f_id);
			}
			account.setAccount_type((int) account_type[i]);
			account.setDate(date[i]);
			accountService.insert(account);
		}
	}

	@RequestMapping(value = "getImage")
	public void getImage(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") int id)
			throws IOException {
		res.setContentType("image/jpeg");
		FilesDto file = fileMapper.getReceiptImage(id);
		byte[] imagefile = file.getData();
		InputStream in1 = new ByteArrayInputStream(imagefile);
		IOUtils.copy(in1, res.getOutputStream());
	}

	@RequestMapping("delete")
	public String delete(Model model, @RequestParam("id") int id, @RequestParam("club_id") int club_id) {
		int f_id = accountMapper.findFileId(id);
		accountMapper.delete(id);
		fileMapper.delete(f_id);
		return "redirect:account#fh5co-tab-feature-center" + club_id;
	}
}
