package migrationStories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP549_CoreSchema_ETQTableExclusion extends Base {

	static Logger log = Logger.getLogger(MMP549_CoreSchema_ETQTableExclusion.class.getName());

	
	/**
	 * This script is to validate all the ETQTables are excluded during the migration process
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyAllETQTablesAreExcludedDuringMigration()
			throws JSchException, SftpException, Exception {
		log.info("TC01_verify if all the ETQTAbles are excluded during the migration...is started....");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP549_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> engineEtqTables = new ArrayList<String>(Arrays.asList("action_types", "additional_sysfields",
				"after_submission_page", "app_search_modes", "assign_users_group", "assignment_filter",
				"assignment_format", "authentication_options", "based_on", "based_on_type", "boolean_values",
				"chart_filters", "charts_display_format", "column_search_types", "compare_options", "compare_types",
				"comparison_operator", "connection_types", "correction_type", "create_phase", "currency_symbols",
				"database_vendors", "datetime_portions", "datetime_styles", "days_of_week", "decision_tree_answers",
				"decision_tree_chooses", "decision_values", "default_app_access", "default_condition", "default_unit",
				"defualt_fromula_type", "dialog_parent_types", "document_formula_type", "document_state",
				"dtf_format_option", "dtf_local_option", "entering_phase_type", "escalation_actions",
				"esignature_icons", "esignature_type", "etq_condition_1", "etq_link_doc_states",
				"etq_window_title_type", "execute_using", "field_delimiter", "field_dialog_type", "field_display_types",
				"field_link_mode", "field_link_types", "field_names_type", "field_required_types", "field_types",
				"file_after_processing", "find_match", "form_history_types", "form_subject_type", "frequency_unit",
				"global_search_modes", "group_routing_type", "handle_records_subf", "handling", "hot_comment_icons",
				"hot_keys", "import_formula_type", "include_exclude_field", "jdbc_source_database",
				"localization_options", "location_profile_srv", "location_security", "logical_operator",
				"lookup_options", "mail_profile_type", "mail_profile_types", "mapping_type", "method", "mt_types",
				"number_charts_row", "number_defulat_type", "number_formats", "numbering_formats", "numbering_type",
				"offline_distribution_types", "oi_sync_options", "onopen_goto", "person_profile_srv",
				"phase_assignment_types", "phase_condition", "phase_directions", "phase_due_date_type",
				"phase_forward_type", "phase_routing_type", "phase_routing_types", "phase_tracker_options",
				"phase_types", "portal_content", "processed_records", "promotion_options", "reader_access",
				"recs_to_delete", "rel_field_etqscript", "search_scopes", "search_types", "select_names_options",
				"set_phase_criteria", "short_list_options", "source_field_formula", "subform_lookup_refresh_options",
				"subform_types", "tagging_option_type", "task_timer_frequencies", "task_week_days", "timezone_options",
				"url_description_type", "users_from_field_options", "view_add_filters_lookup", "view_filters",
				"view_order_types", "view_search_modes", "voting_options", "wf_offline_list", "wf_revision_formats",
				"xml_direction"));
		List<String> targetResultQuery = new ArrayList<>();
		for (String engineTables : engineEtqTables) {
			targetQuery = targetQuery(
					prop.getProperty("tgtquery") + engineTables + " where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetResultQuery.add(String.valueOf(targetQuery.getObject(1)));
			}
			Assert.assertEquals(targetResultQuery.size(), 0);
		}
		List<String> dataCentereEtqTables = new ArrayList<String>(
				Arrays.asList("contact_method", "contact_type", "handle_process_attach", "keyword_sources", "locales",
						"media_type", "operation_method", "operation_parameters", "parameter_style", "time_zones",
						"ws_deployment_status", "pdf_generation_messages"));
		List<String> dataCenterResultQuery = new ArrayList<>();
		for (String dctables : dataCentereEtqTables) {
			targetQuery = targetQuery(
					prop.getProperty("dctgtquery") + dctables + " where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				dataCenterResultQuery.add(String.valueOf(targetQuery.getObject(1)));
			}
			Assert.assertEquals(dataCenterResultQuery.size(), 0);
		}
		log.info("TC01_verify if all the ETQTAbles are excluded during the migration... Ended.......");
	}
}
